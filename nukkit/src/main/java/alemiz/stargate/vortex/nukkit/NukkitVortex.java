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

package alemiz.stargate.vortex.nukkit;

import alemiz.stargate.vortex.client.VortexClient;
import alemiz.stargate.vortex.client.VortexListener;
import alemiz.stargate.vortex.client.data.VortexClientSettings;
import alemiz.stargate.vortex.common.data.CompressionEnum;
import alemiz.stargate.vortex.common.node.VortexNode;
import alemiz.stargate.vortex.minecraft.Minecraft;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;

public class NukkitVortex extends PluginBase implements VortexListener {

    public static final int STARGATE_PROTOCOL = 2;
    private static NukkitVortex instance;

    private final List<VortexListener> pluginListeners = Collections.synchronizedList(new ObjectArrayList<>());
    private VortexClient client;

    @Override
    public void onEnable() {
        if (instance == null) {
            instance = this;
        }

        Minecraft.initClientTypes();

        this.client = new VortexClient(this.createSettings());
        this.client.setListener(this);
        Minecraft.registerPackets(this.client.getPacketPool());
        this.client.start();
    }

    public void onDisable() {
        if (this.client != null && this.client.isConnected()) {
            this.client.shutdown();
        }
    }

    private VortexClientSettings createSettings() {
        this.saveDefaultConfig();
        Config config = this.getConfig();
        InetSocketAddress address = new InetSocketAddress(config.getString("vortexAddress"), config.getInt("vortexPort"));
        CompressionEnum compression = CompressionEnum.valueOf(config.getString("vortexCompression").toUpperCase());

        VortexClientSettings settings = new VortexClientSettings();
        settings.setPort(address.getPort());
        settings.setRemoteAddress(address);
        settings.setPassword(config.getString("vortexPassword"));
        settings.setCompression(compression);
        settings.setCompressionLevel(config.getInt("vortexCompressionLevel"));
        settings.setClientName(config.getString("clientIdentifier"));
        settings.setVortexType(config.getString("vortexType"));
        settings.setMasterNodes(config.getStringList("masterNodes"));
        settings.setPrimaryMasterNode(config.getString("primaryMasterNode"));
        settings.setProtocolversion(STARGATE_PROTOCOL);
        return settings;
    }

    @Override
    public void onNodeCreated(InetSocketAddress address, VortexNode node) {
        for (VortexListener listener : this.pluginListeners) {
            listener.onNodeCreated(address, node);
        }
    }

    @Override
    public void onNodeDisconnected(VortexNode node) {
        for (VortexListener listener : this.pluginListeners) {
            listener.onNodeDisconnected(node);
        }
    }

    public void addNodeListener(VortexListener listener) {
        if (this.client.isConnected()) {
            VortexNode vortexNode = this.client.getVortexNode();
            listener.onNodeCreated(vortexNode.getAddress(), vortexNode);
        }
        this.pluginListeners.add(listener);
    }

    public boolean removeNodeListener(VortexListener listener) {
        return this.pluginListeners.remove(listener);
    }

    public static NukkitVortex getInstance() {
        return instance;
    }

    public VortexClient getClient() {
        return this.client;
    }
}
