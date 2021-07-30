package tk.burdukowsky.rutorparser;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.github.markusbernhardt.proxy.selector.pac.PacScriptMethods;
import com.github.markusbernhardt.proxy.util.ProxyUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    private static final String PAC_SOCKS = "SOCKS";
    private static final String PAC_DIRECT = "DIRECT";

    public static String trimHorizontalWhitespaceChars(String input) {
        return input.replaceAll("(^\\h*)|(\\h*$)", "");
    }

    public static long stringToLong(String input) {
        return Long.parseLong(trimHorizontalWhitespaceChars(input));
    }

    public static List<Proxy> getProxies(String url) throws ScriptException, IOException, NoSuchMethodException {
        // First we get a JavaScript engine
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");

        // Create a Java binding to be used from the JavaScript execution
        engine.put("MyJavaPacImpl", new PacScriptMethods());

        // Add the required JavaScript methods by bridging to the Java binding
        for (Method method : PacScriptMethods.class.getMethods()) {
            String bridgeFunctionDef = defineBridgeFunction(
                    method.getName(),
                    method.getParameterTypes().length);
            engine.eval(bridgeFunctionDef);
        }

        // The engine is now ready to be used to evaluate the PAC script
        var pac = doGet("https://antizapret.prostovpn.org/proxy.pac");
        engine.eval(pac);

        // Now let's use the FindProxyForURL function to get the proxy
        // for the URL we want to access
        Invocable invocableEngine = (Invocable) engine;
        var urlO = new URL(url);
        var result = String.valueOf(invocableEngine.invokeFunction(
                "FindProxyForURL",
                urlO.toString(),
                urlO.getHost()));

        List<Proxy> proxies = new ArrayList<>();
        String[] proxyDefinitions = result.split("[;]");
        for (String proxyDef : proxyDefinitions) {
            if (proxyDef.trim().length() > 0) {
                proxies.add(buildProxyFromPacResult(proxyDef));
            }
        }
        return proxies;
    }

    public static String defineBridgeFunction(String funcName, Integer argCount) {
        var args = getArgs(argCount);
        return funcName + " = function(" + args + ") { return MyJavaPacImpl." + funcName + "(" + args + "); }";
    }

    public static String getArgs(Integer argCount) {
        var sb = new StringBuilder();
        for (int i = 0; i < argCount; i++) {
            sb.append("arg").append(i);
            if (i < argCount - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static String doGet(String path) throws IOException {
        var url = new URL(path);
        var urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setConnectTimeout(10000);
        urlConnection.setRequestMethod("GET");
        var br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        var sb = new StringBuilder();
        String output;
        var lineSeparator = System.lineSeparator();
        while ((output = br.readLine()) != null) {
            sb.append(output);
            sb.append(lineSeparator);
        }
        urlConnection.disconnect();
        return sb.toString();
    }

    public static Proxy buildProxyFromPacResult(String pacResult) {
        if (pacResult.trim().length() < 6) {
            return Proxy.NO_PROXY;
        }
        String proxyDef = pacResult.trim();
        if (proxyDef.toUpperCase().startsWith(PAC_DIRECT)) {
            return Proxy.NO_PROXY;
        }

        // Check proxy type.
        Proxy.Type type = Proxy.Type.HTTP;
        if (proxyDef.toUpperCase().startsWith(PAC_SOCKS)) {
            type = Proxy.Type.SOCKS;
        }

        String host = proxyDef.substring(6);
        int port = ProxyUtil.DEFAULT_PROXY_PORT;

        // Split port from host
        int indexOfPort = host.indexOf(':');
        int index2 = host.lastIndexOf(']');
        if (indexOfPort != -1 && index2 < indexOfPort) {
            port = Integer.parseInt(host.substring(indexOfPort + 1).trim());
            host = host.substring(0, indexOfPort).trim();
        }

        SocketAddress adr = InetSocketAddress.createUnresolved(host, port);
        return new Proxy(type, adr);
    }
}
