import com.amazon.speech.speechlet.dialog.directives.*;
import com.amazon.speech.speechlet.Directive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.speechlet.* ;
import com.amazon.speech.slu.* ;
import com.amazon.speech.json.* ;
import com.amazon.speech.ui.* ;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ProtocolException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

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

        String intentName = (intent != null) ? intent.getName() : null;
        Slot slot = intent.getSlot("currency");

//        DialogState dialogueState = request.getDialogState();

        if ("QueryPriceIntent".equals(intentName)) {
            if (slot.getValue() == null) {
                log.info("Asking slot.");
//                DelegateDirective dd = new DelegateDirective();
                DialogIntent dialogIntent = new DialogIntent();
                dialogIntent.setName(intentName);
                Map<String,DialogSlot> dialogSlots = new HashMap<String,DialogSlot>();
                Iterator iter = intent.getSlots().entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry pair = (Map.Entry)iter.next();
                    DialogSlot dialogSlot = new DialogSlot();
                    Slot mySlot = (Slot) pair.getValue();
                    dialogSlot.setName(mySlot.getName());
                    dialogSlots.put((String) pair.getKey(), dialogSlot);
                }

                dialogIntent.setSlots(dialogSlots);

                ElicitSlotDirective elicitSlotDirective = new ElicitSlotDirective();
                elicitSlotDirective.setSlotToElicit("currency");
                elicitSlotDirective.setUpdatedIntent(dialogIntent);
                List<Directive> directiveList = new ArrayList<Directive>();
                directiveList.add(elicitSlotDirective);

                PlainTextOutputSpeech speech = getPlainTextOutputSpeech("Which currency?");

                SpeechletResponse speechletResponse = new SpeechletResponse();
                speechletResponse.setCard(getSimpleCard("Crypton", "Which currency?"));
                speechletResponse.setOutputSpeech(speech);
                speechletResponse.setDirectives(directiveList);
                speechletResponse.setShouldEndSession(false);
                return speechletResponse;
            }
            else {
                String slotValue = slot.getValue();
                return getPriceResponse(slotValue);
            }
        }
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        }
        else if ("AMAZON.StopIntent".equals((intentName))) {
            return getStopResponse();
        }
        else {
            return getAskResponse("Crypton", "This is unsupported. Please try something else.");
        }
    }

    @Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
         log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                 requestEnvelope.getSession().getSessionId());
        // any cleanup logic goes here
    }

    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to Crypton, you can ask for cryptocurrency prices on Koinex";
        return getAskResponse("Crypton", speechText);
    }

    private SpeechletResponse getStopResponse() {
        String speechText = "See you.";
        return getTellResponse("Crypton", speechText);
    }

    private String getUrlResponse(String urlString) {
        log.info("getUrlResponse url={}", urlString);
        String urlResponse = "";
        try {
            URL url = new URL(urlString);

            String line;
            InputStreamReader inputStream = null;
            BufferedReader bufferedReader = null;
            StringBuilder builder = new StringBuilder();
            inputStream = new InputStreamReader(url.openStream(), Charset.forName("utf-8"));
            bufferedReader = new BufferedReader(inputStream);
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
            urlResponse = builder.toString();
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

//        log.info("getUrlResponse url={} urlResponse={}", urlString, urlResponse);

        return urlResponse;
    }

    private SpeechletResponse getPriceResponse (String slotValue) {

        log.info("getPriceResponse currency={}", slotValue);

        String url = "https://koinex.in/api/ticker";
        String resultString = getUrlResponse(url);

        String symbol = "";
        slotValue = slotValue.toLowerCase();
        if (slotValue.equals("bitcoin"))
            symbol = "BTC";
        else if (slotValue.equals("ether"))
            symbol = "ETH";
        else if (slotValue.equals("ripple"))
            symbol = "XRP";
        else if (slotValue.equals("litecoin"))
            symbol = "LTC";
        else if (slotValue.equals("bitcoin cash"))
            symbol = "BCH";
        else if (slotValue.equals("iota"))
            symbol = "MIOTA";
        else if (slotValue.equals("omisego"))
            symbol = "OMG";
        else if (slotValue.equals("golem"))
            symbol = "GNT";

        String price = "";
        try {
        // parse the output string
        JSONObject responseObject = new JSONObject(resultString).getJSONObject("prices");
        if ("".equals(symbol))
            return getAskResponse("Crypton","Are you sure you said the right name?");
        else
            price = responseObject.getString(symbol).toString();

        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        String speechText = String.format("The price of one %s is %s", slotValue, price);

        return getTellResponse("Crypton", speechText);
    };

    private SpeechletResponse getHelpResponse() {
        String speechText = "You can ask prices for cryptocurrencies. Try asking the price of bitcoin.";
        return getAskResponse("Crypton", speechText);
    }

    private SimpleCard getSimpleCard(String title, String content) {
        SimpleCard card = new SimpleCard();
        card.setTitle(title);
        card.setContent(content);

        return card;
    }

    private PlainTextOutputSpeech getPlainTextOutputSpeech(String speechText) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return speech;
    }

    private Reprompt getReprompt(OutputSpeech outputSpeech) {
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(outputSpeech);

        return reprompt;
    }

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
