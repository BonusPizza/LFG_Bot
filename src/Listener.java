package me.bonuspizza;

import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;

public class Listener extends ListenerAdapter {

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

		if (event.getChannel().getId().equalsIgnoreCase("1136044072642019439")) {
			switch (event.getName()) {
			case "lfg":
				event.deferReply().setEphemeral(true).queue();
				System.out.println(event.getCommandString());
				try {
					event.getChannel()
							.sendMessage(new MessageCreateBuilder()
									.addContent(createEventMessage(event.getCommandString(),
											event.getGuild().getMemberById(event.getUser().getId()).getEffectiveName()))
									.build())
							.complete();

					event.getChannel().getHistory().retrievePast(1).queue(messages -> {
						if (messages.size() > 0) {
							messages.get(0).createThreadChannel(threadNameGenerator(event.getCommandString())).queue();
							messages.get(0).getStartedThread().addThreadMember(event.getUser()).queue();
							messages.get(0).addReaction(Emoji.fromUnicode("U+2705")).queue();
						}
					});
					event.getHook().deleteOriginal().queue();
				} catch (NumberFormatException e) {
					String temp = event.getOption("anzahl").getAsString();
					event.getHook().sendMessage("Die Zahl: " + temp + " ist zu groß").queue();
				}
				break;
			case "deleteall":
				event.deferReply().setEphemeral(true).queue();
				System.out.println(event.getCommandString());
				if (event.getUser().getId().equalsIgnoreCase("330819312631676931")
						|| event.getUser().getId().equalsIgnoreCase("271566632625635328")) {
					if (event.getChannel().getId().equalsIgnoreCase("1136044072642019439")) {
						deleteAll(event);
						event.getHook().deleteOriginal().queue();
					} else {
						event.getHook().editOriginal("Ist in diesem Kanal nicht möglich!").queue();
					}
				} else {
					event.getHook().editOriginal("Du hast nicht die nötige Berechtigung diesen Befehl auszuführen!")
							.queue();
				}

				break;
			}
		} else {
			event.reply("Falscher Kanal").setEphemeral(true).queue();
		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		/*
		 * if (event.getChannel().getId().equalsIgnoreCase("1093631266546532393")) { if
		 * (event.getAuthor().isBot()) { String[] splitMessage =
		 * event.getMessage().getContentRaw().split(" "); for (int i = 0; i <
		 * splitMessage.length; i++) {
		 * 
		 * switch (splitMessage[i].toLowerCase()) { case "<@&989515734419923005>": case
		 * "<@&989516173584527410>": case "<@&989516481723236403>": case
		 * "<@&989516287833153566>": case "<@&989516630700736543>": case
		 * "<@&989516745528188979>": case "<@&989517056519065640>": case
		 * "<@&1076198336186548335>":
		 * event.getMessage().createThreadChannel(threadNameGenerator(event.getMessage()
		 * .getContentRaw())) .queue();
		 * event.getMessage().addReaction(Emoji.fromUnicode("U+2705")).queue(); i =
		 * splitMessage.length; break; } } } }
		 */
	}

	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		if (event.getChannel().getId().equalsIgnoreCase("1136044072642019439")) {
			try {
				if (!event.isFromThread() && !event.getUser().isBot()) {
					if (event.getEmoji().asUnicode().getAsCodepoints().equalsIgnoreCase("U+2705")) {
						Message m = event.retrieveMessage().complete();
						if (m.getStartedThread() != null) {
							m.getStartedThread().addThreadMember(event.getUser()).queue();
						}

						if (m.getAuthor().getAsTag().equalsIgnoreCase("BonusLFGBot#1898")) {

							String newMessage = anmeldung(m.getContentRaw(),
									event.getGuild().getMemberById(event.getUser().getId()).getEffectiveName());
							// System.out.println(event.getGuild().getMemberById(event.getUser().getId()).getEffectiveName());

							m.editMessage(newMessage).complete();
						}
					}
				}
			} catch (IllegalStateException e) {

			}
		}
	}

	private String[] deleteWeitereMitspieler(String content, String author) {
		String front = content.split("\n\\*\\*Weitere Mitspieler:\\*\\* ")[0];
		try {
			String back = content.split("\n\\*\\*Weitere Mitspieler:\\*\\* ")[1];
			String both = front + ", " + back;
			String[] split = both.split(", ");
			String[] output = new String[split.length - 1];
			int j = 0;
			for (int i = 0; i < split.length; i++) {
				if (!split[i].equalsIgnoreCase(author)) {
					output[j] = split[i];
					j++;
				}
			}
			return output;
		} catch (IndexOutOfBoundsException k) {
			String[] split = front.split(", ");
			String[] output = new String[split.length - 1];
			int j = 0;
			for (int i = 0; i < split.length; i++) {
				if (!split[i].equalsIgnoreCase(author)) {
					output[j] = split[i];
					j++;
				}
			}
			return output;
		}
	}

	private String[] deleteWeitereMitspieler(String content) {
		String front = content.split("\n\\*\\*Weitere Mitspieler:\\*\\* ")[0];
		try {
			String back = content.split("\n\\*\\*Weitere Mitspieler:\\*\\* ")[1];
			String both = front + ", " + back;
			String[] output = both.split(", ");
			return output;
		} catch (IndexOutOfBoundsException i) {
			String[] output = front.split(", ");
			return output;
		}
	}

	private int maxAttendants(String header) {
		try {
			return Integer.parseInt("" + header.charAt(header.length() - 2));
		} catch (NumberFormatException nf) {
			return 0;
		}
	}

	private String anmeldung(String message, String author) {
		String output = "";

		String mitspieler = "**Mitspieler:** ";

		String header = message.split("\\*\\*Mitspieler:\\*\\* ")[0];
		int max = maxAttendants(header);

		output = header + mitspieler;

		String content = message.split("\\*\\*Mitspieler:\\*\\* ")[1];
		String[] arrcontent = deleteWeitereMitspieler(content);

		if (content.equalsIgnoreCase(".")) {
			output += author;
		} else if (max != 0 && arrcontent.length + 1 > max) {

			for (int i = 0; i < max - 1; i++) {
				output += arrcontent[i] + ", ";
			}
			output += arrcontent[max - 1];
			output += "\n**Weitere Mitspieler:** ";
			for (int i = max; i < arrcontent.length; i++) {
				if (!arrcontent[i].equalsIgnoreCase("")) {
					output += arrcontent[i] + ", ";
				}
			}
			output += author;

		} else {
			for (int i = 0; i < arrcontent.length; i++) {
				output += arrcontent[i] + ", ";
			}
			output += author;
		}

		return output;
	}

	public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
		if (event.getChannel().getId().equalsIgnoreCase("1136044072642019439")) {
			try {
				if (event.getEmoji().asUnicode().getAsCodepoints().equalsIgnoreCase("U+2705")) {
					User u = event.retrieveUser().complete();
					if (!event.isFromThread() && !u.isBot()) {
						Message m = event.retrieveMessage().complete();
						if (m.getStartedThread() != null) {
							m.getStartedThread().removeThreadMemberById(event.getUserId()).queue();
						}
						if (m.getAuthor().getAsTag().equalsIgnoreCase("BonusLFGBot#1898")) {

							String newMessage = abmeldung(m.getContentRaw(), event.getGuild()
									.getMemberById(event.retrieveUser().complete().getId()).getEffectiveName());

							m.editMessage(newMessage).complete();

						}
					}
				}
			} catch (IllegalStateException e) {

			}
		}
	}

	private String abmeldung(String message, String author) {
		String output = "";

		String mitspieler = "**Mitspieler:** ";

		String header = message.split("\\*\\*Mitspieler:\\*\\* ")[0];
		int max = maxAttendants(header);

		output = header + mitspieler;

		String content = message.split("\\*\\*Mitspieler:\\*\\* ")[1];
		String[] arrcontent = deleteWeitereMitspieler(content, author);

		if (arrcontent.length == 0) {
			output += ".";
			return output;
		}
		boolean first = true;
		for (int i = 0; i < arrcontent.length; i++) {
			if (i == max && max != 0) {
				output += "\n**Weitere Mitspieler:** ";
				first = true;
			}
			if (first) {
				output += arrcontent[i];
				first = false;
			} else {
				output += ", ";
				output += arrcontent[i];
			}
		}
		return output;

	}

	private String createEventMessage(String input, String author) {

		String ohneThread = input.split(" threadname:")[0];
		String[] split = ohneThread.split(" ");

		String name = " ";
		for (int i = 6; i < split.length; i++) {
			if (!split[i].equals("wann:")) {
				name += split[i] + " ";
			} else {
				i = split.length;
			}
		}
		String output = split[2] + name;
		if (input.contains("wann:")) {
			output += "**Wann:**" + ohneThread.split("wann:")[1] + "\n";
		} else {
			output += "\n";
		}
		int anzahl = Math.abs(Integer.parseInt(split[4]));
		if (anzahl == 0) {
			output += "**Fireteam:** " + author + "\n**Mitspieler:** .";
		} else {
			output += "**Fireteam:** " + author + " | +" + anzahl + "\n**Mitspieler:** .";
		}

		return output;
	}

	private String threadNameGenerator(String in) {

		String out;
		try {
			out = in.split("threadname: ")[1];

		} catch (ArrayIndexOutOfBoundsException i) {
			String split = in.split("name: ")[1];
			out = split.split("wann: ")[0];
		}

		if (out.length() > 100) {
			out = out.substring(0, 100);
		}
		return out;
	}

	@SuppressWarnings("static-access")
	private void deleteAll(SlashCommandInteractionEvent event) {
		List<Message> history = event.getChannel().getHistory().getHistoryFromBeginning(event.getChannel()).complete()
				.getRetrievedHistory();
		try {
			for (int i = event.getOption("before").getAsInt(); i < history.size(); i++) {
				if (!history.get(i).getId().equalsIgnoreCase("1149007456375156746")) {
					if (history.get(i).getStartedThread() != null) {
						history.get(i).getStartedThread().delete().complete();
					}
					event.getChannel().deleteMessageById(history.get(i).getId()).queue();
				}
			}
		} catch (NullPointerException e) {
			for (int i = 0; i < history.size(); i++) {
				if (!history.get(i).getId().equalsIgnoreCase("1149007456375156746")) {
					if (history.get(i).getStartedThread() != null) {
						history.get(i).getStartedThread().delete().complete();
					}
					try {
						event.getChannel().deleteMessageById(history.get(i).getId()).queue();
					} catch (ErrorResponseException c) {
					}
				}
			}
		}
	}

	public void onMessageDelete(MessageDeleteEvent event) {
		if (event.getChannel().getId().equalsIgnoreCase("1136044072642019439")) {
			if (!event.isFromThread()) {
				List<ThreadChannel> tca = event.getGuildChannel().asThreadContainer().getThreadChannels();
				for (int i = 0; i < tca.size(); i++) {
					try {
						tca.get(i).retrieveParentMessage().complete();
					} catch (ErrorResponseException e) {
						tca.get(i).delete().complete();
					}
				}
			}
		}
	}
}
