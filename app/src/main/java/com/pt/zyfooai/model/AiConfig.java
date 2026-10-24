package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * AI config from {@code GET /api/ai/config} or getAppInfo → AI.
 */
public class AiConfig {

    @SerializedName(value = "ai_enabled", alternate = {"enabled", "aiEnabled"})
    public Object aiEnabledRaw;

    @SerializedName(value = "default_provider", alternate = {"defaultProvider"})
    public String defaultProvider;

    @SerializedName(value = "require_package", alternate = {"requirePackage", "package_required"})
    public Object requirePackageRaw;

    @SerializedName(value = "providers", alternate = {"provider_list"})
    public JsonElement providersRaw;

    @SerializedName(value = "tools", alternate = {"features", "tools_list", "tool_list"})
    public JsonElement toolsRaw;

    @SerializedName(value = "openai_enabled", alternate = {"openaiEnabled"})
    public Object openaiEnabledRaw;

    @SerializedName(value = "gemini_enabled", alternate = {"geminiEnabled"})
    public Object geminiEnabledRaw;

    @SerializedName(value = "quota", alternate = {"usage", "limits"})
    public AiQuota quota;

    @SerializedName("message")
    public String message;

    @SerializedName(value = "success", alternate = {"status"})
    public Object successRaw;

    public boolean isEnabled() {
        return isTruthy(aiEnabledRaw) && !resolvedProviders().isEmpty();
    }

    public boolean requirePackage() {
        return isTruthy(requirePackageRaw);
    }

    public List<String> resolvedProviders() {
        ArrayList<String> out = new ArrayList<>();
        if (providersRaw != null && providersRaw.isJsonArray()) {
            for (JsonElement el : providersRaw.getAsJsonArray()) {
                if (el == null || el.isJsonNull()) {
                    continue;
                }
                if (el.isJsonPrimitive()) {
                    addProvider(out, el.getAsString());
                } else if (el.isJsonObject()) {
                    JsonElement id = el.getAsJsonObject().get("id");
                    if (id == null) {
                        id = el.getAsJsonObject().get("name");
                    }
                    if (id == null) {
                        id = el.getAsJsonObject().get("provider");
                    }
                    if (id != null && id.isJsonPrimitive()) {
                        addProvider(out, id.getAsString());
                    }
                }
            }
        }
        if (out.isEmpty()) {
            if (isTruthy(openaiEnabledRaw)) {
                out.add("openai");
            }
            if (isTruthy(geminiEnabledRaw)) {
                out.add("gemini");
            }
        }
        return out;
    }

    public List<String> resolvedTools() {
        ArrayList<String> out = new ArrayList<>();
        if (toolsRaw == null || toolsRaw.isJsonNull()) {
            return out;
        }
        if (toolsRaw.isJsonArray()) {
            for (JsonElement el : toolsRaw.getAsJsonArray()) {
                if (el == null || el.isJsonNull()) {
                    continue;
                }
                if (el.isJsonPrimitive()) {
                    addTool(out, el.getAsString());
                } else if (el.isJsonObject()) {
                    JsonElement id = el.getAsJsonObject().get("id");
                    if (id == null) {
                        id = el.getAsJsonObject().get("key");
                    }
                    if (id == null) {
                        id = el.getAsJsonObject().get("name");
                    }
                    if (id == null) {
                        id = el.getAsJsonObject().get("type");
                    }
                    Object enabled = null;
                    if (el.getAsJsonObject().has("enabled")) {
                        enabled = el.getAsJsonObject().get("enabled");
                    }
                    if (enabled != null && !isTruthy(enabled)) {
                        continue;
                    }
                    if (id != null && id.isJsonPrimitive()) {
                        addTool(out, id.getAsString());
                    }
                }
            }
        } else if (toolsRaw.isJsonObject()) {
            for (String key : toolsRaw.getAsJsonObject().keySet()) {
                if (isTruthy(toolsRaw.getAsJsonObject().get(key))) {
                    addTool(out, key);
                }
            }
        }
        return out;
    }

    public boolean hasTool(String tool) {
        if (tool == null) {
            return false;
        }
        String t = tool.trim().toLowerCase();
        for (String s : resolvedTools()) {
            if (t.equals(s)) {
                return true;
            }
        }
        // If tools empty but AI on — allow chat by default.
        return resolvedTools().isEmpty() && ("chat".equals(t) || "type".equals(t));
    }

    public String resolveDefaultProvider() {
        List<String> providers = resolvedProviders();
        if (providers.isEmpty()) {
            return "";
        }
        if (defaultProvider != null) {
            String d = normalizeProvider(defaultProvider);
            if (providers.contains(d)) {
                return d;
            }
        }
        return providers.get(0);
    }

    private static void addProvider(List<String> out, String raw) {
        String p = normalizeProvider(raw);
        if (!p.isEmpty() && !out.contains(p)) {
            out.add(p);
        }
    }

    private static void addTool(List<String> out, String raw) {
        if (raw == null) {
            return;
        }
        String t = raw.trim().toLowerCase().replace(' ', '_');
        if (t.isEmpty()) {
            return;
        }
        if ("chatgpt".equals(t) || "gpt".equals(t)) {
            return;
        }
        if (!out.contains(t)) {
            out.add(t);
        }
    }

    public static String normalizeProvider(String raw) {
        if (raw == null) {
            return "";
        }
        String p = raw.trim().toLowerCase();
        if ("chatgpt".equals(p) || "gpt".equals(p) || "open_ai".equals(p)) {
            return "openai";
        }
        if ("google".equals(p) || "bard".equals(p)) {
            return "gemini";
        }
        return p;
    }

    public static boolean isTruthy(Object raw) {
        if (raw == null) {
            return false;
        }
        if (raw instanceof Boolean) {
            return (Boolean) raw;
        }
        if (raw instanceof Number) {
            return ((Number) raw).intValue() == 1;
        }
        if (raw instanceof JsonElement) {
            JsonElement el = (JsonElement) raw;
            if (el.isJsonPrimitive()) {
                if (el.getAsJsonPrimitive().isBoolean()) {
                    return el.getAsBoolean();
                }
                if (el.getAsJsonPrimitive().isNumber()) {
                    return el.getAsInt() == 1;
                }
                return isTruthy(el.getAsString());
            }
            return false;
        }
        String s = String.valueOf(raw).trim();
        return "1".equals(s) || "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s) || "on".equalsIgnoreCase(s);
    }
}
