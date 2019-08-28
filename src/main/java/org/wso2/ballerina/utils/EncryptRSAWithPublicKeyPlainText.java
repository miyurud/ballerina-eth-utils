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

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BString;
import org.ballerinalang.natives.annotations.Argument;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.ReturnType;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Native function wso2.ballerina.util:encryptRSAWithPublicKeyPlainText.
 */
@BallerinaFunction(
        orgName = "wso2",
        packageName = "utils:0.0.1",
        functionName = "encryptRSAWithPublicKeyPlainText",
        args = {@Argument(name = "a", type = TypeKind.STRING)},
        returnType = {@ReturnType(type = TypeKind.STRING)},
        isPublic = true
)
public class EncryptRSAWithPublicKeyPlainText extends BlockingNativeCallableUnit {

    @Override
    public void execute(Context context) {
        String publicKey = context.getStringArgument(0).trim();
        String data = context.getStringArgument(1);

        publicKey = "-----BEGIN+RSA+PUBLIC+KEY-----MIIBCgKCAQEAiwh1JK5pgblpHiNX3CycsCYumCvN5hLdLZ00tZ8jUzsDmfQseNk4TxM2pj9Q8NtOro1QxHDMalLbtqpnDbQKdPQm2QvLc3d%2Fzw0BeCNtrEzR0yGjV6WaRzz3b1RrDSmPe27BNIN9qsaABsNSFbAGNsWE8VPsd5bTu2piIoSp8XgMhFCCCBcRNyPdBGoGhiNobdW56qJzSuAjwwx%2B5ug7O2%2FMfReVeYLVUgG7zwuPQqzg9qKj5K3dPA6wOYb5hFQSkEZc%2BhVYXYe6%2FP207hk29JPX%2FOKHe24cXum4BGLPKyzQSqel31GfhkdeIc4AoYFVNKPOsxQeyidFBJ4OsgtucQIDAQAB-----END+RSA+PUBLIC+KEY-----";

        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey.getBytes());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pk = keyFactory.generatePublic(keySpec);

            cipher.init(Cipher.ENCRYPT_MODE, pk);

            String encryptedString = new String(cipher.doFinal(data.getBytes()), "UTF-8");
            BString returnVal = new BString(encryptedString);
            context.setReturnValues(returnVal);
        } catch (Exception e) {
            e.printStackTrace();
            BString returnVal = new BString("Error: " + e.getMessage());
            context.setReturnValues(returnVal);
        }
    }
}