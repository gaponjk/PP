public class Main {
    public static void main(String[] args) {
        FileProcessor fileProcessor = new FileProcessor.Builder()
                .setInputFilePath("input.txt")
                .setOutputFilePath("output.txt")
                .build();
        fileProcessor.extract();
        fileProcessor.push();
    }
}
