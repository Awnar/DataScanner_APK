package pl.awnar.DataScanner.api.model;

public class Data {
    public DataArray[] Data;
    public String TIME;
    public String ERROR;

    public static class DataArray {
        public String server_ID;
        public String in_blob;
        public String in_blob_type;
        public String out_blob;
        public String out_blob_type;
        public String create;
        public String update;
    }
}
