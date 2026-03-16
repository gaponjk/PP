public class JSONWriter {
    private JSONWriter(Builder builder){

    }
    public static class Builder {
        private String inputFilePath;
        private String outputFilePath;

        public JSONWriter.Builder setInputFilePath(String inputFilePath) {
            this.inputFilePath = inputFilePath;
            return this;
        }

        public JSONWriter.Builder setOutputFilePath(String outputFilePath) {
            this.outputFilePath = outputFilePath;
            return this;
        }

        public JSONWriter build() {
            if (inputFilePath == null || outputFilePath == null) {
                throw new IllegalStateException("Не указаны пути для входного или выходного файла!");
            }
            return new JSONWriter(this);
        }
    }
}
