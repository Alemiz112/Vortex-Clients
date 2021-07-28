/*
 * Copyright 2021 Alemiz
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package alemiz.stargate.vortex.waterdogpe;

import alemiz.stargate.vortex.common.node.VortexNode;
import alemiz.stargate.vortex.minecraft.protocol.VortexMinecraftPacketListener;
import alemiz.stargate.vortex.minecraft.protocol.packet.PlayerTransferPacket;
import dev.waterdog.waterdogpe.network.serverinfo.ServerInfo;
import dev.waterdog.waterdogpe.player.ProxiedPlayer;

public class PacketListener implements VortexMinecraftPacketListener {

    private final WaterVortex loader;
    private final VortexNode node;

    public PacketListener(WaterVortex loader, VortexNode node) {
        this.loader = loader;
        this.node = node;
    }

    @Override
    public boolean handlePlayerTransfer(PlayerTransferPacket packet) {
        ProxiedPlayer player = this.loader.getProxy().getPlayer(packet.getPlayerIdentifier());
        ServerInfo serverInfo = this.loader.getProxy().getServerInfo(packet.getTargetServer());
        if (player == null || serverInfo == null){
            return false;
        }
        player.connect(serverInfo);
        return true;
    }
}
