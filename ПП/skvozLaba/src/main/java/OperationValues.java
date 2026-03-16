import java.util.ArrayList;

public class OperationValues {
    private static ArrayList<Double> result=new ArrayList<>();

    public static ArrayList<Double> getResult() {
        return result;
    }

    public static void setResult(ArrayList<Double> result) {
        OperationValues.result = result;
    }
}
