package org.tlabs.comm.grpc.b.data.net;

public class NetInfo {

    private String host;
    private Integer port;


    private NetInfo() {
    }

    private NetInfo(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public static class Builder {

        private String host;
        private Integer port;

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public NetInfo build() {

            return new NetInfo(this.host, this.port);
        }
    }

    @Override
    public String toString() {
        return "NetInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
