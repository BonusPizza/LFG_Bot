package me.bonuspizza;

import java.util.EnumSet;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class AundOBot {

	JDA bot;

	public AundOBot() throws LoginException {
		String token = "MTA5MzYzNDkwOTExNTAwNzA1Nw.GbKpFj.8NXJPG_arceIXhs8jE86oOH7cS-1bNxRA_ddKA";
		bot = (JDA) JDABuilder.createDefault(token).enableIntents(EnumSet.allOf(GatewayIntent.class))
				.addEventListeners(new Listener()).setActivity(Activity.playing("Type /lfg"))
				.setMemberCachePolicy(MemberCachePolicy.ALL).build();
		try {
			bot.awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		bot.updateCommands()
				.addCommands(
						Commands.slash("lfg", "Looking for Guardians")
								.addOptions(new OptionData(OptionType.STRING, "rolle",
										"Wähle eine Rolle die du Pingen willst.", true)
												.addChoice("PvE", "<@&989515734419923005>")
												.addChoice("Raids-Dungeons", "<@&989516287833153566>")
												.addChoice("Nightfall", "<@&989516481723236403>")
												.addChoice("PvP", "<@&989516173584527410>")
												.addChoice("Gambit", "<@&989517056519065640>")
												.addChoice("Trials", "<@&989516630700736543>")
												.addChoice("Iron Banner", "<@&989516745528188979>")
												.addChoice("MonsterHunter", "<@&1076198336186548335>"))
								.addOption(OptionType.INTEGER, "anzahl",
										"Wie viele Mitspieler suchst du? 0 Bedeutet egal wie viele Teilnehmer.", true)
								.addOption(OptionType.STRING, "name", "Was willst du spielen?", true)
								.addOption(OptionType.STRING, "wann", "Wann willst du starten?", false)
								.addOption(OptionType.STRING, "threadname", "Soll der Thread einen besonderen Namen bekommen?", false),
						Commands.slash("deleteall", "Lösche alle Nachrichten").addOption(OptionType.INTEGER, "before",
								"gib die Anzahl der letzten x Nachrichten die nicht gelöscht werden sollen", false))
				.queue();
	}

	public static void main(String[] args) {

		Gui g = new Gui(200, 100);
		try {
			AundOBot b = new AundOBot();
			b.run();
		} catch (LoginException e) {
			e.printStackTrace();
			g.error();
		}

	}

}
