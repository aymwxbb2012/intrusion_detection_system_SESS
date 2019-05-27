package cdu.edu.chao.utils;

import cdu.edu.chao.GsonUtil;
import cdu.edu.chao.domain.DetectionReport;
import cdu.edu.chao.domain.TrainingReport;
import org.apache.commons.io.FileUtils;
import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.records.reader.impl.transform.TransformProcessRecordReader;
import org.datavec.api.split.FileSplit;
import org.datavec.api.transform.TransformProcess;
import org.datavec.api.transform.schema.Schema;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.iterator.DataSetIteratorSplitter;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.io.ClassPathResource;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.nd4j.evaluation.classification.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class DNNUtil {


    public static void main(String[] args) throws Exception {

        initializeModel(false);
        long start = System.currentTimeMillis();
        training("/Test+SubDataSet1.csv", 1200);
        long end = System.currentTimeMillis();
        double diff = (double) (end - start) / 1000;
        System.out.println("time spand : " + diff);

    }


    public static TransformProcess initializeTransformProcess() {
        Schema schema = new Schema.Builder()
                .addColumnInteger("duration")
                .addColumnCategorical("protocol_type", Arrays.asList("tcp", "udp", "icmp"))
                .addColumnCategorical("service", Arrays.asList("ftp_data", "other", "private", "http", "remote_job", "name", "netbios_ns", "eco_i", "mtp", "telnet", "finger", "domain_u", "supdup", "uucp_path", "Z39_50", "smtp", "csnet_ns", "uucp", "netbios_dgm", "urp_i", "auth", "domain", "ftp", "bgp", "ldap", "ecr_i", "gopher", "vmnet", "systat", "http_443", "efs", "whois", "imap4", "iso_tsap", "echo", "klogin", "link", "sunrpc", "login", "kshell", "sql_net", "time", "hostnames", "exec", "ntp_u", "discard", "nntp", "courier", "ctf", "ssh", "daytime", "shell", "netstat", "pop_3", "nnsp", "IRC", "pop_2", "printer", "tim_i", "pm_dump", "red_i", "netbios_ssn", "rje", "X11", "urh_i", "http_8001", "tftp_u"))
                .addColumnCategorical("flag", Arrays.asList("SF", "S0", "REJ", "RSTR", "SH", "RSTO", "S1", "RSTOS0", "S3", "S2", "OTH"))
                .addColumnsDouble("src_bytes", "dst_bytes")
                .addColumnCategorical("land", Arrays.asList("0", "1"))
                .addColumnsDouble("wrong_fragment", "urgent", "hot", "num_failed_logins")
                .addColumnCategorical("logged_in", Arrays.asList("0", "1"))
                .addColumnsDouble("num_compromised", "root_shell", "su_attempted", "num_root", "num_file_creations", "num_shells", "num_access_files", "num_outbound_cmds")
                .addColumnCategorical("is_host_login", Arrays.asList("0", "1"))
                .addColumnCategorical("is_guest_login", Arrays.asList("0", "1"))
                .addColumnsDouble("count", "srv_count", "serror_rate", "srv_serror_rate", "rerror_rate", "srv_rerror_rate", "same_srv_rate", "diff_srv_rate", "srv_diff_host_rate")
                .addColumnsDouble("dst_host_count", "dst_host_srv_count", "dst_host_same_srv_rate", "dst_host_diff_srv_rate", "dst_host_same_src_port_rate", "dst_host_srv_diff_host_rate", "dst_host_serror_rate", "dst_host_srv_serror_rate", "dst_host_rerror_rate", "dst_host_srv_rerror_rate")
                .addColumnCategorical("class", Arrays.asList("normal", "anomaly"))
                .build();
        TransformProcess transformProcess = new TransformProcess.Builder(schema)
                .categoricalToOneHot("protocol_type", "service", "flag")
                .removeColumns("protocol_type[icmp]", "service[http]", "flag[SF]")
                .categoricalToInteger("class")
                .build();
        String serializedTransformString = transformProcess.toJson();

        try {

            File file = new File("transformProcess.json");

            if (!file.exists()) {

                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter("transformProcess.json");
            fileWriter.write(serializedTransformString);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transformProcess;
    }

    public static TransformProcess getTransformProcess() {

        try {
            String transformProcessString = new String(Files.readAllBytes(Paths.get("transformProcess.json")));
            return TransformProcess.fromJson(transformProcessString);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DataSetIterator normalization(String filepath, int batchSize, int labelIndex, int numClasses) {
        final String filename;
        try {
            filename = new ClassPathResource(filepath).getFile().getPath();


            RecordReader rr = new CSVRecordReader(1, ',');

            rr.initialize(new FileSplit(new File(filename)));
            TransformProcess transformProcess = initializeTransformProcess();
            RecordReader transformProcessRecordReader = new TransformProcessRecordReader(rr, transformProcess); //Passing transformation process to convert the csv file
            DataSetIterator iter = new RecordReaderDataSetIterator(transformProcessRecordReader, batchSize, labelIndex, numClasses);
            DataNormalization dataNormalization = new NormalizerStandardize();
            dataNormalization.fit(iter);
            iter.setPreProcessor(dataNormalization);
            return iter;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Model initializeModel(boolean openAnalysisCharts) {

        int seed = 123;
        int batchSize = 48;
        int batchNumber = 300;
        int epoch = 100;

        int numInputs = 116;
        int numOutputs = 1;
        int numHiddenNodes = 150;

        int labelIndex = 116;
        int numClasses = 1;
        DataSetIterator trainIter = normalization("/KDDTrain+_20Percent.csv", batchSize, labelIndex, numClasses);

        DataSetIteratorSplitter splitter = new DataSetIteratorSplitter(trainIter, batchNumber, 0.7);


        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam())
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(numInputs)
                        .nOut(50)
                        .activation(Activation.RELU)
                        .build())
                .layer(new DenseLayer.Builder().nIn(50).nOut(100).build()) //hidden layer
                .layer(new DenseLayer.Builder().nIn(100).nOut(100).dropOut(0.1).build()) //hidden layer
                .layer(new DenseLayer.Builder().nIn(100).nOut(50).build()) //hidden layer
                .layer(new OutputLayer.Builder(LossFunction.XENT)
                        .nIn(50)
                        .nOut(numOutputs)
                        .activation(Activation.SIGMOID)
                        .build())
                .build();


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        if (openAnalysisCharts) {

            UIServer uiServer = UIServer.getInstance();


            StatsStorage statsStorage = new InMemoryStatsStorage();
            int listenerFrequency = 1;
            model.setListeners(new StatsListener(statsStorage, listenerFrequency));

            uiServer.attach(statsStorage);
        }
        model.fit(splitter.getTrainIterator(), epoch);

        Evaluation evaluation = model.evaluate(splitter.getTestIterator());

        System.out.println("args = " + evaluation.stats() + "");

        File locationToSave = new File("SESSMultiLayerNetwork.zip");
        boolean saveUpdater = true;
        try {
            ModelSerializer.writeModel(model, locationToSave, saveUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

    public static MultiLayerNetwork getModel() {

        try {
            MultiLayerNetwork restoredModel = ModelSerializer.restoreMultiLayerNetwork("SESSMultiLayerNetwork.zip");
            return restoredModel;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static DetectionReport detection(String dataPath, int dataNumber) {
        int labelIndex = 116;
        int numClasses = 1;

        DataSetIterator detectionIterator = normalization(dataPath, dataNumber, labelIndex, numClasses);
        DataSet ds = detectionIterator.next();

        MultiLayerNetwork detectionModel = getModel();
        INDArray predictedResult = detectionModel.output(ds.getFeatures(), false);
        Evaluation eval = new Evaluation(numClasses);
        INDArray labels = ds.getLabels();

        int attackNumber = 0;
        int i;
        for (i = 0; i < predictedResult.size(0); i++) {
            INDArray a = predictedResult.getRow(i);
            Long b = Math.round(a.getDouble(0, 0));
            if (b == 1L)
                attackNumber++;
        }

        DetectionReport detectionReport = new DetectionReport(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()), attackNumber, dataNumber);

        return detectionReport;

    }

    public static TrainingReport training(String dataPath, int dataNumber) {
        int labelIndex = 116;
        int numClasses = 1;


        DataSetIterator detectionIterator = normalization(dataPath, dataNumber, labelIndex, numClasses);
        DataSet ds = detectionIterator.next();

        MultiLayerNetwork detectionModel = getModel();
        INDArray predictedResult = detectionModel.output(ds.getFeatures(), false);
        Evaluation eval = new Evaluation(numClasses);
        INDArray labels = ds.getLabels();

        File locationToSave = new File("SESSMultiLayerNetwork.zip");
        boolean saveUpdater = true;
        try {
            ModelSerializer.writeModel(detectionModel, locationToSave, saveUpdater);
        } catch (IOException e) {
            e.printStackTrace();
        }
        eval.eval(labels, predictedResult);
        System.out.println("args = " + eval.stats() + "");
        TrainingReport trainingReport = new TrainingReport(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()), eval.accuracy());
        return trainingReport;
    }


}
