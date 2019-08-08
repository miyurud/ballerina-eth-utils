/*
*   Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.ballerina.utils;

import org.apache.commons.codec.binary.Base64;
import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import java.io.PrintStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;


/**
 * Native function wso2.ballerina.util:decryptAes.
 */
@BallerinaFunction(
        orgName = "wso2",
        packageName = "utils:0.0.1",
        functionName = "writeBlockchainRecord",
        args = {@Argument(name = "key", type = TypeKind.STRING),
                @Argument(name = "value", type = TypeKind.STRING),
                @Argument(name = "blockChainAPIURL", type = TypeKind.STRING),
                @Argument(name = "account", type = TypeKind.STRING),
                @Argument(name = "password", type = TypeKind.STRING),
                @Argument(name = "workDirectory", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true
)
public class BlockChainInterface extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String key = context.getStringArgument(0).trim();
        String value = context.getStringArgument(1).trim();
        String blockChainAPIURL = context.getStringArgument(2).trim();
        String account = context.getStringArgument(3).trim();
        String password = context.getStringArgument(4).trim();
        String workDirectory = context.getStringArgument(5).trim();

        System.out.println("key: " + key);
        System.out.println("value: " + value);
        System.out.println("blockChainAPIURL: " + blockChainAPIURL);
        System.out.println("account: " + account);
        System.out.println("password: " + password);
        System.out.println("workDirectory: " + workDirectory);


        String result = writeBlockchainRecord(key, value, blockChainAPIURL, account, password, workDirectory);

        BString returnVal = new BString(result);
        context.setReturnValues(returnVal);
    }

    public static String writeBlockchainRecord(String key, String value, String blockChainAPIURL, String account, String password, String workDirectory) {
        String s = null;

        try {
            Process p = Runtime.getRuntime().exec("geth --unlock \""+ account +"\" --password \"" + password + "\" attach " + blockChainAPIURL);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                s = s.trim();

                if (s.equals("modules: eth:1.0 net:1.0 personal:1.0 rpc:1.0 web3:1.0")) {
                    break;
                }
            }

            OutputStream outStrm = p.getOutputStream();
            PrintStream prtStrm = new PrintStream(outStrm);
            //We need to unlock the account
            prtStrm.println("personal.unlockAccount(eth.accounts[0])");
            prtStrm.flush();

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);

                if (s.contains("password will be")){
                    break;
                }
            }

            prtStrm.println("1234");
            prtStrm.flush();

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                if (s.equals("true")) {
                    break;
                }
            }

            prtStrm.println("loadScript(\"" + workDirectory + "/simplestorage.js\")");
            prtStrm.println("\r\n");
            prtStrm.flush();

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                int index = s.indexOf("Contract mined! address: ");
                if (index != -1) {
                    System.out.println("index:" + index);

                    break;
                }

                index = s.indexOf("exceeds block gas limit undefined");
                if (index != -1) {
                    System.out.println("index:" + index);

                    return "-1";
                }
            }

            s = s.substring(25, 67);

            return s;
        }
        catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}