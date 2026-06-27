package eu.endermite.censura.listener;

import eu.endermite.censura.Censura;
import eu.endermite.censura.PlayerChatHistory;
import eu.endermite.censura.filter.Filter;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimilarMessageListener implements Listener {
    private final JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
    private final Map<UUID, PlayerChatHistory> chatHistory = new ConcurrentHashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChatSimilarEvent(AsyncPlayerChatEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        PlayerChatHistory messages = chatHistory.get(playerUuid);
        String currentMessage = event.getMessage().toLowerCase();

        if (messages == null) {
            PlayerChatHistory newPlayerChatHistory = new PlayerChatHistory();
            newPlayerChatHistory.addMessage(currentMessage);
            chatHistory.put(playerUuid, newPlayerChatHistory);
            return;
        }

        if (Filter.isExempt(event.getPlayer())) return;

        int threshold = Censura.getCachedConfig().getSimilarMessageThreshold();
        int maxSimilar = Censura.getCachedConfig().getSimilarMaxMessages();

        int similarCount = 0;

        for (String oldMessage : messages.getMessageHistory()) {
            int percent = (int) Math.round(similarity.apply(oldMessage, currentMessage) * 100);

            if (percent >= threshold) {
                similarCount++;
            }
            if (similarCount >= maxSimilar) {
                break;
            }
        }

        if (similarCount >= maxSimilar) {
            event.setCancelled(true);
            Filter.doActions(Censura.getCachedConfig().getSimilarCheckActions(), event.getPlayer());
            return;
        }
        messages.addMessage(currentMessage);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        chatHistory.remove(event.getPlayer().getUniqueId());
    }

}
