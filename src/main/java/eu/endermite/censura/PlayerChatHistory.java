package eu.endermite.censura;

import java.util.ArrayList;

public class PlayerChatHistory {

    private final ArrayList<String> messageHistory = new ArrayList<>();

    public void addMessage(String string) {
        if (Censura.getCachedConfig().getSimilarMessageThreshold() == null) return;
        if (Censura.getCachedConfig().getSimilarMessageAmount() == null) return;
        if (messageHistory.size() >= Censura.getCachedConfig().getSimilarMessageAmount()) {
            messageHistory.remove(0);
        }
        messageHistory.add(string);
    }

    /**
     * Returns a copy of message history
     * @return A copy of message history
     */
    public ArrayList<String> getMessageHistory() {
        return messageHistory;
    }
}
