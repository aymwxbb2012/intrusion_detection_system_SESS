package cdu.edu.chao.exceptions;

public class BlockChainException extends RuntimeException {
    private String retCd ;
    private String msgDes;

    public BlockChainException() {
        super();
    }

    public BlockChainException(String message) {
        super(message);
        msgDes = message;
    }

    public BlockChainException(String retCd, String msgDes) {
        super();
        this.retCd = retCd;
        this.msgDes = msgDes;
    }

    public String getRetCd() {
        return retCd;
    }

    public String getMsgDes() {
        return msgDes;
    }
}
