from keras.constraints import maxnorm
from sklearn import preprocessing
import pandas as pd
import io
import requests
import numpy as np
import os
from sklearn.model_selection import train_test_split
from sklearn import metrics
from keras.models import Sequential
from keras.layers.core import Dense, Activation, Dropout
from keras.callbacks import EarlyStopping
from keras.optimizers import Adamax
import numpy
from keras.callbacks import Callback
from sklearn.metrics import f1_score, precision_score, recall_score


def one_hot_encode_text(df, name):
    dummies = pd.get_dummies(df[name])
    for x in dummies.columns:
        dummy_name = "{}-{}".format(name, x)
        df[dummy_name] = dummies[x]
    df.drop(name, axis=1, inplace=True)


def zscore_encode_numeric(df, name, mean=None, sd=None):
    if mean is None:
        mean = df[name].mean()

    if sd is None:
        sd = df[name].std()

    df[name] = (df[name] - mean) / sd


def encode_text_classes(df, name):
    le = preprocessing.LabelEncoder()
    df[name] = le.fit_transform(df[name])


def get_normalised_data(filePath):
    raw = pd.read_csv(filePath, header=None)
    print("Read {} rows.".format(len(raw)))
    raw.dropna(inplace=True, axis=1)

    raw.columns = [
        'duration',
        'protocol_type',
        'service',
        'flag',
        'src_bytes',
        'dst_bytes',
        'land',
        'wrong_fragment',
        'urgent',
        'hot',
        'num_failed_logins',
        'logged_in',
        'num_compromised',
        'root_shell',
        'su_attempted',
        'num_root',
        'num_file_creations',
        'num_shells',
        'num_access_files',
        'num_outbound_cmds',
        'is_host_login',
        'is_guest_login',
        'count',
        'srv_count',
        'serror_rate',
        'srv_serror_rate',
        'rerror_rate',
        'srv_rerror_rate',
        'same_srv_rate',
        'diff_srv_rate',
        'srv_diff_host_rate',
        'dst_host_count',
        'dst_host_srv_count',
        'dst_host_same_srv_rate',
        'dst_host_diff_srv_rate',
        'dst_host_same_src_port_rate',
        'dst_host_srv_diff_host_rate',
        'dst_host_serror_rate',
        'dst_host_srv_serror_rate',
        'dst_host_rerror_rate',
        'dst_host_srv_rerror_rate',
        'class'
    ]

    zscore_encode_numeric(raw, 'duration')
    one_hot_encode_text(raw, 'protocol_type')
    one_hot_encode_text(raw, 'service')
    one_hot_encode_text(raw, 'flag')
    zscore_encode_numeric(raw, 'src_bytes')
    zscore_encode_numeric(raw, 'dst_bytes')
    one_hot_encode_text(raw, 'land')
    zscore_encode_numeric(raw, 'wrong_fragment')
    zscore_encode_numeric(raw, 'urgent')
    zscore_encode_numeric(raw, 'hot')
    zscore_encode_numeric(raw, 'num_failed_logins')
    one_hot_encode_text(raw, 'logged_in')
    zscore_encode_numeric(raw, 'num_compromised')
    zscore_encode_numeric(raw, 'root_shell')
    zscore_encode_numeric(raw, 'su_attempted')
    zscore_encode_numeric(raw, 'num_root')
    zscore_encode_numeric(raw, 'num_file_creations')
    zscore_encode_numeric(raw, 'num_shells')
    zscore_encode_numeric(raw, 'num_access_files')
    zscore_encode_numeric(raw, 'num_outbound_cmds')
    one_hot_encode_text(raw, 'is_host_login')
    one_hot_encode_text(raw, 'is_guest_login')
    zscore_encode_numeric(raw, 'count')
    zscore_encode_numeric(raw, 'srv_count')
    zscore_encode_numeric(raw, 'serror_rate')
    zscore_encode_numeric(raw, 'srv_serror_rate')
    zscore_encode_numeric(raw, 'rerror_rate')
    zscore_encode_numeric(raw, 'srv_rerror_rate')
    zscore_encode_numeric(raw, 'same_srv_rate')
    zscore_encode_numeric(raw, 'diff_srv_rate')
    zscore_encode_numeric(raw, 'srv_diff_host_rate')
    zscore_encode_numeric(raw, 'dst_host_count')
    zscore_encode_numeric(raw, 'dst_host_srv_count')
    zscore_encode_numeric(raw, 'dst_host_same_srv_rate')
    zscore_encode_numeric(raw, 'dst_host_diff_srv_rate')
    zscore_encode_numeric(raw, 'dst_host_same_src_port_rate')
    zscore_encode_numeric(raw, 'dst_host_srv_diff_host_rate')
    zscore_encode_numeric(raw, 'dst_host_serror_rate')
    zscore_encode_numeric(raw, 'dst_host_srv_serror_rate')
    zscore_encode_numeric(raw, 'dst_host_rerror_rate')
    zscore_encode_numeric(raw, 'dst_host_srv_rerror_rate')
    encode_text_classes(raw, 'class')
    raw.dropna(inplace=True, axis=1)
    return raw


def separate_parameters_and_classes(df, target):
    result = []
    for x in df.columns:
        if x != target:
            result.append(x)
    target_type = df[target].dtypes
    target_type = target_type[0] if hasattr(target_type, '__iter__') else target_type
    if target_type in (np.int64, np.int32):
        dummies = pd.get_dummies(df[target])
        return df.as_matrix(result).astype(np.float32), dummies.as_matrix().astype(np.float32)
    else:
        return df.as_matrix(result).astype(np.float32), df.as_matrix([target]).astype(np.float32)


raw = get_normalised_data('KDDTrain+_20Percent.csv')

x_train, y_train = separate_parameters_and_classes(raw, 'class')


x_train, x_test, y_train, y_test = train_test_split(
    x_train, y_train, test_size=0.40, random_state=42)



model = Sequential()
model.add(Dense(50, input_dim=x_train.shape[1], kernel_initializer='glorot_normal', activation='relu'))
model.add(Dense(100, activation='relu', W_constraint=maxnorm(3)))
model.add(Dense(100, activation='relu', W_constraint=maxnorm(3)))
model.add(Dense(100, activation='relu', W_constraint=maxnorm(3)))
model.add(Dense(50, activation='relu'))
model.add(Dense(y_train.shape[1], activation='softmax'))
model.compile(loss='categorical_crossentropy', optimizer='Adamax')
monitor = EarlyStopping(monitor='val_loss', min_delta=1e-3, patience=5, verbose=1, mode='auto')
model.fit(x_train, y_train, validation_data=(x_test, y_test), callbacks=[monitor], verbose=2, epochs=1000)
pred = model.predict(x_test)
pred = np.argmax(pred, axis=1)
y_eval = np.argmax(y_test, axis=1)
# show report
score = metrics.accuracy_score(y_eval, pred)
# confusion=metrics.confusion_matrix(y_eval,pred)
# numpy.savetxt( 'D:\data\\new.csv', y_eval, delimiter = ',')
target_names = ['class 0']
report = metrics.classification_report(y_eval, pred)
print("Validation score: {}".format(score))
print(report)
'''
test1=get_normalised_data('E:\data\KDDTestd+.csv')
x_train1, y_train1 = utils.to_xy(test1, 'class')
pred1 = model.predict(x_train1)
pred1 = np.argmax(pred1, axis=1)
y_eval1 = np.argmax(y_train1, axis=1)
score1 = metrics.accuracy_score(y_eval1, pred1)
# confusion=metrics.confusion_matrix(y_eval,pred)
# numpy.savetxt( 'D:\data\\new.csv', y_eval, delimiter = ',')
target_names = ['class 0']
report = metrics.classification_report(y_eval1, pred1)
print("Validation score1: {}".format(score1))
print(report)
'''
