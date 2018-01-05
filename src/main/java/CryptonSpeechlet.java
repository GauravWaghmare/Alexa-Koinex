import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.speechlet.* ;
import com.amazon.speech.slu.* ;
import com.amazon.speech.json.* ;
import com.amazon.speech.ui.* ;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ProtocolException;
import org.json.JSONObject;
import org.json.JSONException;

public class CryptonSpeechlet implements SpeechletV2 {
    private static final Logger log = LoggerFactory.getLogger(CryptonSpeechlet.class);

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                requestEnvelope.getSession().getSessionId());

        Intent intent = request.getIntent();
        Slot currencySlot = intent.getSlot("currency");

        String intentName = (intent != null) ? intent.getName() : null;
        String slotValue = (currencySlot != null) ? currencySlot.getValue() : null;

        if ("QueryPriceIntent".equals(intentName)) {
            return getPriceResponse(slotValue);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            return getAskResponse("Crypton", "This is unsupported.  Please try something else.");
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
         log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                 requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the Crypton, you can ask for cryptocurrency prices on Koinex";
        return getAskResponse("Crypton", speechText);
    }

    private String getUrlResponse(String urlString) {
        log.info("getUrlResponse url={}", urlString);
        String urlResponse = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            log.info("HTTP response code={}", connection.getResponseCode());

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder results = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                results.append(line);
            }
            urlResponse = results.toString();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        log.info("getUrlResponse url={} urlResponse={}", urlString, urlResponse);
        return urlResponse;
    }

    private SpeechletResponse getPriceResponse (String slotValue) {

        log.info("getPriceResponse currency={}", slotValue);

        String url = "https://koinex.in/api/ticker";
        String resultString = getUrlResponse(url);

        String symbol = "";
        slotValue = slotValue.toLowerCase();
        if (slotValue == "bitcoin")
            symbol = "BTC";
        else if (slotValue == "ether")
            symbol = "ETH";
        else if (slotValue == "ripple")
            symbol = "XRP";
        else if (slotValue == "litecoin")
            symbol = "LTC";
        else if (slotValue == "bitcoin cash")
            symbol = "BCH";
        else if (slotValue == "iota")
            symbol = "MIOTA";
        else if (slotValue == "omisego")
            symbol = "OMG";
        else if (slotValue == "golem")
            symbol = "GNT";

        String price = "";
        try {
        // parse the output string
        JSONObject responseObject = new JSONObject(resultString).getJSONObject("prices");
        price = responseObject.getString(symbol).toString();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        String speechText = String.format("The price of one %s is %s", slotValue, price);

        return getTellResponse("Crypton", speechText);
    };

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";
        return getAskResponse("HelloWorld", speechText);
    }

    /**
     * Helper method that creates a card object.
     * @param title title of the card
     * @param content body of the card
     * @return SimpleCard the display card to be sent along with the voice response.
     */
    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    /**
     * Helper method for retrieving an OutputSpeech object when given a string of TTS.
     * @param speechText the text that should be spoken out to the user.
     * @return an instance of SpeechOutput.
     */
    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    /**
     * Helper method that returns a reprompt object. This is used in Ask responses where you want
     * the user to be able to respond to your speech.
     * @param outputSpeech The OutputSpeech object that will be said once and repeated if necessary.
     * @return Reprompt instance.
     */
    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

    /**
     * Helper method for retrieving an Ask response with a simple card and reprompt included.
     * @param cardTitle Title of the card that you want displayed.
     * @param speechText speech text that will be spoken to the user.
     * @return the resulting card and speech text.
     */
    private SpeechletResponse getAskResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);
        Reprompt reprompt = getReprompt(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    private SpeechletResponse getTellResponse(String cardTitle, String speechText) {
        SimpleCard card = getSimpleCard(cardTitle, speechText);
        PlainTextOutputSpeech speech = getPlainTextOutputSpeech(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

}
