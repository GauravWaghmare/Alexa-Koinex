/*
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import org.junit.Test;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import static com.amazon.speech.json.SpeechletRequestEnvelope.*;
import static org.junit.Assert.*;

public class CryptonSpeechletTest {

    @Test
    public void onIntent() {
        String json = "{\r\n  \"session\": {\r\n    \"new\": true,\r\n    \"sessionId\": \"SessionId.fa04a616-2ab7-4266-be3e-176a3bdeb23d\",\r\n    \"application\": {\r\n      \"applicationId\": \"amzn1.ask.skill.9c4eb38d-ddbd-424e-bac7-0e5e3d0fd325\"\r\n    },\r\n    \"attributes\": {},\r\n    \"user\": {\r\n      \"userId\": \"amzn1.ask.account.AFNFWYCYWKEGZRNMXJZ6Z6NEQ566AB6QQFWTXTVQLASX4UOA6XGBKMZEQDPBYL7CPMW53LLX62YJBKIZUWFACUZYSIGGKMSK4RNEVRD4LKSYBTRXOUCXCARQ4I43MYMMHFDI6I2OOEUSHIPBARV34QMP4PWENJQ23JV2V5NYGK67N4QHA7GPWE2XHYECG473H2DNYFR3MUBK42Y\"\r\n    }\r\n  },\r\n  \"request\": {\r\n    \"type\": \"IntentRequest\",\r\n    \"requestId\": \"EdwRequestId.1ce01483-b9c4-4d01-b1e0-b6aca4e0a052\",\r\n    \"intent\": {\r\n      \"name\": \"QueryPriceIntent\",\r\n      \"slots\": {\r\n        \"currency\": {\r\n          \"name\": \"currency\",\r\n          \"value\": \"bitcoin\"\r\n        }\r\n      }\r\n    },\r\n    \"locale\": \"en-IN\",\r\n    \"timestamp\": \"2018-01-05T10:33:27Z\"\r\n  },\r\n  \"context\": {\r\n    \"AudioPlayer\": {\r\n      \"playerActivity\": \"IDLE\"\r\n    },\r\n    \"System\": {\r\n      \"application\": {\r\n        \"applicationId\": \"amzn1.ask.skill.9c4eb38d-ddbd-424e-bac7-0e5e3d0fd325\"\r\n      },\r\n      \"user\": {\r\n        \"userId\": \"amzn1.ask.account.AFNFWYCYWKEGZRNMXJZ6Z6NEQ566AB6QQFWTXTVQLASX4UOA6XGBKMZEQDPBYL7CPMW53LLX62YJBKIZUWFACUZYSIGGKMSK4RNEVRD4LKSYBTRXOUCXCARQ4I43MYMMHFDI6I2OOEUSHIPBARV34QMP4PWENJQ23JV2V5NYGK67N4QHA7GPWE2XHYECG473H2DNYFR3MUBK42Y\"\r\n      },\r\n      \"device\": {\r\n        \"supportedInterfaces\": {}\r\n      }\r\n    }\r\n  },\r\n  \"version\": \"1.0\"\r\n}";
        try {
            JSONObject jsonObject = new JSONObject(json);
            // SpeechletResponse testResponse = onIntent((SpeechletRequestEnvelope<IntentRequest>) jsonObject);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Builder<IntentRequest> builder = new SpeechletRequestEnvelope.builder();
    }
}*/
