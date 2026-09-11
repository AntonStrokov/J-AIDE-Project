package com.antonstrokov.jaide.plugin.service;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

public final class JaidePluginMetadataService {

	private static final PluginId JAIDE_PLUGIN_ID =
			PluginId.getId("com.antonstrokov.jaide");

	private static final String UNKNOWN_VERSION = "unknown";

	public String getPluginVersion() {
		IdeaPluginDescriptor pluginDescriptor =
				PluginManagerCore.getPlugin(JAIDE_PLUGIN_ID);

		if (pluginDescriptor == null) {
			return UNKNOWN_VERSION;
		}

		String version = pluginDescriptor.getVersion();

		if (version == null || version.isBlank()) {
			return UNKNOWN_VERSION;
		}

		return version;
	}
}
