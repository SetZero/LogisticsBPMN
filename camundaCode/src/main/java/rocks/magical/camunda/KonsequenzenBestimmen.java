package rocks.magical.camunda;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.logging.Logger;

public class KonsequenzenBestimmen implements JavaDelegate {
    private final Logger LOGGER = Logger.getLogger(KonsequenzenBestimmen.class.getName());

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String responseString = execution.getVariable("response").toString();

        Type responseType = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> response = new Gson().fromJson(responseString, responseType);

        Map<String, Map<String, Double>> grades = (Map<String, Map<String, Double>>) response.get("noten");

        double sum = 0;
        for(String module: grades.keySet()) {
            sum += grades.get(module).get("note");
        }
        double avg = sum / grades.size();

        LOGGER.info("--------------> AVG:" + avg);
        execution.setVariable("avgGrade", avg);
    }
}
