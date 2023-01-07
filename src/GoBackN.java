public class GoBackN implements ARQ {
    private final Socket socket;

    public GoBackN(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean data_req(byte[] hlData, int hlSize, boolean lastTransmission) {
        return false;
    }

    @Override
    public byte[] data_ind_req(int... values) throws TimeoutException {
        return new byte[0];
    }

    @Override
    public int getBackData() {
        return 0;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public int getRetransmissionCounter() {
        return 0;
    }

    @Override
    public int getRetryCounterStat() {
        return 0;
    }

    @Override
    public int getMTU() {
        return 0;
    }

    @Override
    public void setMTU(int MTU) {

    }

}
