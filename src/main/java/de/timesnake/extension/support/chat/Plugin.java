package de.timesnake.extension.support.chat;

public class Plugin extends de.timesnake.basic.bukkit.util.chat.Plugin {

    public static final Plugin SUPPORT = new Plugin("Support", "EXS");

    protected Plugin(String name, String code) {
        super(name, code);
    }
}
