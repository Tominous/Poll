package com.github.mrivanplays.poll;

import com.github.mrivanplays.poll.commands.CommandPoll;
import com.github.mrivanplays.poll.commands.CommandPollSend;
import com.github.mrivanplays.poll.commands.CommandPollVotes;
import com.github.mrivanplays.poll.question.QuestionAnnouncer;
import com.github.mrivanplays.poll.question.QuestionHandler;
import com.github.mrivanplays.poll.storage.SerializableQuestion;
import com.github.mrivanplays.poll.storage.SerializableQuestions;
import com.github.mrivanplays.poll.storage.VotersFile;
import com.github.mrivanplays.poll.util.MetricsSetup;
import com.github.mrivanplays.poll.util.UpdateCheckerSetup;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public final class Poll extends JavaPlugin {

    @Getter
    private VotersFile votersFile;
    @Getter
    private QuestionHandler questionHandler;
    private QuestionAnnouncer announcer;

    public static Function<String, String> ANSWER_FUNCTION = answer -> ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', answer));

    @Override
    public void onEnable() {
        saveDefaultConfig();
        votersFile = new VotersFile(getDataFolder());
        if (!votersFile.deserialize().isEmpty()) {
            for (SerializableQuestion question : votersFile.deserialize()) {
                SerializableQuestions.register(question);
            }
        }
        questionHandler = new QuestionHandler(this);
        new CommandPoll(this);
        new CommandPollVotes(this);
        new CommandPollSend(this);
        announcer = new QuestionAnnouncer(this);
        announcer.loadAsAnnouncements();
        new MetricsSetup(this).setup();
        getLogger().info("Plugin enabled");
        // The task is executed on another thread
        // Starts after 5 minutes and repeats every 30 minutes
        // Can't harm the server while saving things into a file.
        getServer().getScheduler().runTaskTimerAsynchronously(this, () ->
                votersFile.serialize(SerializableQuestions.getForSerialize()), 300 * 20, 600 * 3 * 20);
        new UpdateCheckerSetup(this, "poll.updatenotify").setup();
    }

    @Override
    public void onDisable() {
        votersFile.serialize(SerializableQuestions.getForSerialize());
        getLogger().info("Plugin disabled");
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void reload() {
        reloadConfig();
        announcer.reloadAnnouncements();
        votersFile.serialize(SerializableQuestions.getForSerialize());
    }
}
