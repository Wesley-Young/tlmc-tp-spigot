package cn.timelessmc.teleport.gui;

import cn.timelessmc.teleport.home.HomeEntrySubMap;
import cn.timelessmc.teleport.warp.WarpEntryMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class BedrockGUIFactory {
    private static BedrockGUIFactory instance;
    private final WarpEntryMap warpEntryMap;
    private final Map<String, HomeEntrySubMap> homeEntryMap;

    public BedrockGUIFactory(WarpEntryMap warps, Map<String, HomeEntrySubMap> homes) {
        warpEntryMap = warps;
        homeEntryMap = Collections.synchronizedMap(homes);
        instance = this;
    }

    public static BedrockGUIFactory getInstance() {
        return instance;
    }

    public Form createRootForm(FloodgatePlayer player) {
        return SimpleForm.builder()
                .title("Snowball Menu")
                .button("Warp To...")
                .button("Go Home")
                .button("Teleport to Player")
                .button("Modify Home List")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> player.sendForm(createWarpForm(player, false));
                        case 1 -> player.sendForm(createHomeForm(player, false));
                        case 2 -> player.sendForm(createTPARequestForm(player, false));
                        case 3 -> player.sendForm(createHomeModificationForm(player, false));
                    }
                }).build();
    }

    public Form createRootFormForOP(FloodgatePlayer player) {
        return SimpleForm.builder()
                .title("Snowball Menu (ServerOp)")
                .button("Warp To...")
                .button("Go Home")
                .button("Teleport to Player")
                .button("Modify Warp List")
                .button("Modify Home List")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> player.sendForm(createWarpForm(player, true));
                        case 1 -> player.sendForm(createHomeForm(player, true));
                        case 2 -> player.sendForm(createTPARequestForm(player, true));
                        case 3 -> player.sendForm(createWarpModificationForm(player));
                        case 4 -> player.sendForm(createHomeModificationForm(player, true));
                    }
                }).build();
    }

    public Form createTPAConfirmationForm(FloodgatePlayer player, String tpaExecutor) {
        Player e = toBukkitPlayer(player);
        return ModalForm.builder()
                .title("TPA Confirmation")
                .content("Player §6" + tpaExecutor + " §rsent a request to teleport to you.")
                .button1("Accept")
                .button2("Deny").validResultHandler((response) -> e.performCommand(response.clickedFirst() ? "tpaccept" : "tpdeny")).build();
    }

    public Player toBukkitPlayer(@NotNull FloodgatePlayer player) {
        return Bukkit.getPlayer(player.getCorrectUniqueId());
    }

    public FloodgatePlayer toFloodgatePlayer(@NotNull Player player) {
        return FloodgateApi.getInstance().getPlayer(player.getUniqueId());
    }

    private @NotNull Form createWarpForm(FloodgatePlayer player, boolean playerIsOp) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title("Warp To...")
                .button("Back");

        for (String entry : warpEntryMap.keySet()) {
            builder.button(entry);
        }

        builder.validResultHandler((response) -> {
            if (response.clickedButtonId() == 0) {
                player.sendForm(playerIsOp ? createRootFormForOP(player) : createRootForm(player));
            } else {
                Player e = toBukkitPlayer(player);
                e.performCommand("warp " + response.clickedButton().text());
            }
        });
        return builder.build();
    }

    private @NotNull Form createHomeForm(FloodgatePlayer player, boolean playerIsOp) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title("Go Home")
                .button("Back");
        HomeEntrySubMap subMap = homeEntryMap.get(player.getCorrectUniqueId().toString());

        for (String entry : subMap.keySet()) {
            builder.button(entry);
        }

        builder.validResultHandler((response) -> {
            if (response.clickedButtonId() == 0) {
                player.sendForm(playerIsOp ? createRootFormForOP(player) : createRootForm(player));
            } else {
                Player e = toBukkitPlayer(player);
                e.performCommand("home " + response.clickedButton().text());
            }
        });
        return builder.build();
    }

    private @NotNull Form createTPARequestForm(FloodgatePlayer player, boolean playerIsOp) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title("Teleport to Player")
                .button("Back");

        for (Player e : Bukkit.getOnlinePlayers()) {
            builder.button(e.getName());
        }

        builder.validResultHandler((response) -> {
            if (response.clickedButtonId() == 0) {
                player.sendForm(playerIsOp ? createRootFormForOP(player) : createRootForm(player));
            } else {
                toBukkitPlayer(player).performCommand("tpa " + response.clickedButton().text());
            }

        });
        return builder.build();
    }

    private @NotNull Form createWarpModificationForm(FloodgatePlayer player) {
        return SimpleForm.builder()
                .title("Modify Warp List")
                .button("Back")
                .button("Add...")
                .button("Delete...")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> player.sendForm(createRootFormForOP(player));
                        case 1 -> player.sendForm(createWarpAddingForm(player));
                        case 2 -> player.sendForm(createWarpDeletingForm(player));
                    }

                }).build();
    }

    private @NotNull Form createWarpAddingForm(FloodgatePlayer player) {
        return CustomForm.builder()
                .title("Add...")
                .dropdown("Type", warpEntryMap.getTypes())
                .input("Name")
                .input("Description", "Don't be too lazy to fill in this!")
                .validResultHandler((response) -> {
                    int typeIndex = response.asDropdown();
                    String name = Objects.requireNonNull(response.asInput());
                    String description = Objects.requireNonNull(response.asInput());
                    Player e = toBukkitPlayer(player);
                    if (warpEntryMap.containsKey(name)) {
                        player.sendForm(ModalForm.builder().title("Error At").content("A warp entry of this name already exists!").button1("Back").button2("Exit").validResultHandler((_response) -> {
                            if (_response.clickedFirst()) {
                                player.sendForm(createWarpAddingForm(player));
                            }

                        }));
                    } else if (name.isBlank() | description.isBlank()) {
                        player.sendForm(ModalForm.builder()
                                .title("Error At")
                                .content("LAZY!!")
                                .button1("Back")
                                .button2("Exit")
                                .validResultHandler((_response) -> {
                                    if (_response.clickedFirst()) {
                                        player.sendForm(createWarpAddingForm(player));
                                    }
                                }));
                    } else {
                        e.performCommand("setwarp " + warpEntryMap.getTypes().get(typeIndex) + " " + name + " " + description);
                    }
                }).build();
    }

    private @NotNull Form createWarpDeletingForm(FloodgatePlayer player) {
        SimpleForm.Builder builder = SimpleForm.builder()
                .title("Delete...")
                .button("Back");

        for (String entry : warpEntryMap.keySet()) {
            builder.button(entry);
        }

        builder.validResultHandler((response) -> {
            if (response.clickedButtonId() == 0) {
                player.sendForm(createWarpModificationForm(player));
            } else {
                player.sendForm(ModalForm.builder()
                        .title("Confirm")
                        .content("Do you really want to delete the warp entry '" + response.clickedButton().text() + "'?").button1("Yes").button2("No").validResultHandler((_response) -> {
                            if (_response.clickedFirst()) {
                                toBukkitPlayer(player).performCommand("delwarp " + response.clickedButton().text());
                            } else {
                                player.sendForm(createWarpDeletingForm(player));
                            }
                        }).build());
            }
        });
        return builder.build();
    }

    private @NotNull Form createHomeModificationForm(FloodgatePlayer player, boolean playerIsOp) {
        return SimpleForm.builder()
                .title("Modify Home List")
                .button("Back")
                .button("Add...")
                .button("Delete...")
                .validResultHandler((response) -> {
                    switch (response.clickedButtonId()) {
                        case 0 -> player.sendForm(playerIsOp ? createRootFormForOP(player) : createRootForm(player));
                        case 1 -> player.sendForm(createHomeAddingForm(player));
                        case 2 -> player.sendForm(createHomeDeletingForm(player, playerIsOp));
                    }
                }).build();
    }

    private @NotNull Form createHomeAddingForm(FloodgatePlayer player) {
        return CustomForm.builder()
                .title("Add...")
                .input("Name")
                .validResultHandler((response) -> {
                    String name = Objects.requireNonNull(response.asInput());
                    Player e = Objects.requireNonNull(Bukkit.getPlayer(player.getCorrectUniqueId()));
                    if (name.isBlank()) {
                        player.sendForm(ModalForm.builder().title("Error At").content("Empty Name!").button1("Back").button2("Exit").validResultHandler((_response) -> {
                            if (_response.clickedFirst()) {
                                player.sendForm(createHomeAddingForm(player));
                            }
                        }));
                    } else {
                        e.performCommand("sethome " + name);
                    }
                }).build();
    }

    private @NotNull Form createHomeDeletingForm(@NotNull FloodgatePlayer player, boolean playerIsOp) {
        SimpleForm.Builder builder = SimpleForm.builder().title("Delete...").button("Back");
        Player e = Objects.requireNonNull(Bukkit.getPlayer(player.getCorrectUniqueId()));
        HomeEntrySubMap subMap = homeEntryMap.get(e.getUniqueId().toString());

        for (String entry : subMap.keySet()) {
            builder.button(entry);
        }

        builder.validResultHandler((response) -> {
            if (response.clickedButtonId() == 0) {
                player.sendForm(createHomeModificationForm(player, playerIsOp));
            } else {
                player.sendForm(ModalForm.builder().title("Confirm").content("Do you really want to delete the home entry '" + response.clickedButton().text() + "'?").button1("Yes").button2("No").validResultHandler((_response) -> {
                    if (_response.clickedFirst()) {
                        toBukkitPlayer(player).performCommand("delhome " + response.clickedButton().text());
                    } else {
                        player.sendForm(createHomeDeletingForm(player, playerIsOp));
                    }
                }).build());
            }
        });
        return builder.build();
    }
}
