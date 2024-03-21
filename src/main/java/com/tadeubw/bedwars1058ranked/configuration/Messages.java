package com.tadeubw.bedwars1058ranked.configuration;

import com.andrei1058.bedwars.api.language.Language;
import org.jetbrains.annotations.NotNull;

public class Messages {

    @SuppressWarnings("WeakerAccess")
    public static final String PATH = "addons.ranked.";
    public static final String PLAYER_NOT_FOUND = PATH + "player-not-found";
    public static final String CMD_NOT_FOUND = PATH + "cmd-not-found";

    public static void setupMessages() {
        for (Language l : Language.getLanguages()) {
            addDefault(l, PLAYER_NOT_FOUND, "&cPlayer not found or offline", "&cJogador não encontrado ou offline");
            addDefault(l, CMD_NOT_FOUND, "&cCommand not found or you do not have permission!", "&cComando não encontrado ou você não tem permissão!");
        }
    }

    private static void addDefault(@NotNull Language l, String path, Object english, Object portuguese) {
        if (!l.exists(path)) {
            l.set(path, l.getIso().equals("pt") ? portuguese.toString() : english.toString());
        }
    }
}
