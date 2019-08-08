public extern function hexToChar(string a) returns string;
public extern function decToChar(int a) returns string;
public extern function encryptRSAWithPublicKey(string key, string value) returns string;
public extern function encryptAes(string valueToEncode, string key) returns string;
public extern function decryptAes(string valueToDecode, string key) returns string;
public extern function writeBlockchainRecord(string key, string value, string blockChainAPIURL, string account,
string password, string workDirectory) returns string;
public extern function hashSHA256(string input) returns string;