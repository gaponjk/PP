import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileProcessor {
    private final String inputFilePath;
    private final String outputFilePath;

    private FileProcessor(Builder builder) {
        this.inputFilePath = builder.inputFilePath;
        this.outputFilePath = builder.outputFilePath;
    }

    private FileProcessor(String input, String output) {
        this.inputFilePath = input;
        this.outputFilePath = output;
    }

    public void extract() {
        try {
            String inputData = readFromFile(inputFilePath);
            inputData = processSqrt(inputData);
            OperationValues.setResult(processArithmeticOperationsWithRegex(inputData.trim()));

        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка при работе с файлами: " + e.getMessage());
        }
    }
    public void push(){

    }
    private String readFromFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    private void writeToFile(String filePath, ArrayList<Double>arrayList) throws IOException {
        Files.writeString(Path.of(filePath), arrayList.toString());
    }

    //решаю все корни внутри
    private String processSqrt(String inputData) {
        Pattern pattern = Pattern.compile("sqrt\\((\\s*\\d+(\\.\\d+)?\\s*)\\)");
        Matcher matcher = pattern.matcher(inputData);

        StringBuilder result = new StringBuilder();
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        while (matcher.find()) {
            double x = Double.parseDouble(matcher.group(1));
            double sqrtValue = Math.sqrt(x);
            matcher.appendReplacement(result, String.valueOf(decimalFormat.format(sqrtValue)));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private ArrayList<Double> processArithmeticOperationsWithRegex(String data) {
        ArrayList<Double> arrayList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\(\\s*)?((\\d+(\\.\\d+)?\\s*\\)*\\s*[+\\-*/]\\s*)+(\\d+(\\.\\d+)?))");
        Matcher matcher = pattern.matcher(data);

        while (matcher.find()) {
            String expression = matcher.group();
            arrayList.add(evaluate(expression));
        }
        return arrayList;
    }

    private double evaluate(String expression) {
        expression = expression.replaceAll("\\s+", "");

        List<String> postfix = convertToPostfix(expression);

        return evaluatePostfix(postfix);
    }

    //RPN
    private List<String> convertToPostfix(String expression) {
        Stack<Character> operators = new Stack<>();
        List<String> output = new ArrayList<>();
        StringBuilder number = new StringBuilder();

        for (char c : expression.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } else {
                if (number.length() > 0) {
                    output.add(number.toString());
                    number.setLength(0);
                }
                if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.pop();
                } else if (isOperator(c)) {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        output.add(String.valueOf(operators.pop()));
                    }
                    operators.push(c);
                }
            }
        }

        if (number.length() > 0) {
            output.add(number.toString());
        }

        while (!operators.isEmpty()) {
            output.add(String.valueOf(operators.pop()));
        }

        return output;
    }

    private double evaluatePostfix(List<String> postfix) {
        Stack<Double> stack = new Stack<>();

        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else {
                double b = stack.pop();
                double a = stack.pop();
                switch (token) {
                    case "+" -> stack.push(a + b);
                    case "-" -> stack.push(a - b);
                    case "*" -> stack.push(a * b);
                    case "/" -> stack.push(a / b);
                    default -> throw new IllegalArgumentException("Unknown operator: " + token);
                }
            }
        }

        return stack.pop();
    }

    private boolean isNumber(String token) {
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            default -> -1;
        };
    }

    public static class Builder {
        private String inputFilePath;
        private String outputFilePath;

        public Builder setInputFilePath(String inputFilePath) {
            this.inputFilePath = inputFilePath;
            return this;
        }

        public Builder setOutputFilePath(String outputFilePath) {
            this.outputFilePath = outputFilePath;
            return this;
        }

        public FileProcessor build() {
            if (inputFilePath == null || outputFilePath == null) {
                throw new IllegalStateException("Не указаны пути для входного или выходного файла!");
            }
            return new FileProcessor(this);
        }
    }
}

