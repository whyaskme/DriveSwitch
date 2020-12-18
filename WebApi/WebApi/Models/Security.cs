using System;
using System.Linq;
using System.Text;
using System.IO;

using System.Security.Cryptography;

namespace WebApi.Models
{
    public class Security
    {
        #region Hashing methods

        private static byte[] GetHash(string inputString)
        {
            HashAlgorithm algorithm = MD5.Create();
            return algorithm.ComputeHash(Encoding.UTF8.GetBytes(inputString));
        }

        public static string GetHashString(string inputString)
        {
            var sb = new StringBuilder();
            foreach (byte b in GetHash(inputString))
                sb.Append(b.ToString("X2"));

            return sb.ToString();
        }

        private static string CreateMD5Hash(string input)
        {
            // Use input string to calculate MD5 hash
            MD5 md5 = System.Security.Cryptography.MD5.Create();
            byte[] inputBytes = System.Text.Encoding.ASCII.GetBytes(input);
            byte[] hashBytes = md5.ComputeHash(inputBytes);

            // Convert the byte array to hexadecimal string
            var sb = new StringBuilder();
            for (var i = 0; i < hashBytes.Length; i++)
            {
                //sb.Append(hashBytes[i].ToString("X2"));
                // To force the hex string to lower-case letters instead of
                // upper-case, use he following line instead:
                sb.Append(hashBytes[i].ToString("x2"));
            }
            return sb.ToString();
        }
        #endregion

        #region Encryption
        public static string EncryptAndEncode(string plaintext, string key)
        {
            byte[] myKey = UTF8Encoding.UTF8.GetBytes(key.Substring(0, 16));
            return ByteArrayToHexString(AesEncrypt(plaintext, myKey));
        }
        public static string DecodeAndDecrypt(string cipherText, string key)
        {
            byte[] myKey;
            try
            {
                myKey = UTF8Encoding.UTF8.GetBytes(key.Substring(0, 16));
            }
            catch
            {
                return "";
            }
            string DecodeAndDecrypt = AesDecrypt(StringToByteArray(cipherText), myKey);
            return (DecodeAndDecrypt);
        }

        private static string ByteArrayToHexString(byte[] ba)
        {
            return BitConverter.ToString(ba).Replace("-", "");
        }

        private static byte[] StringToByteArray(string hex)
        {
            return Enumerable.Range(0, hex.Length)
                                .Where(x => x % 2 == 0)
                                .Select(x => Convert.ToByte(hex.Substring(x, 2), 16))
                                .ToArray();
        }

        private static string AesDecrypt(Byte[] inputBytes, Byte[] keyAndIvBytes)
        {
            Byte[] outputBytes = inputBytes;
            var plaintext = string.Empty;
            var mException = "";
            try
            {
                using (MemoryStream memoryStream = new MemoryStream(outputBytes))
                {
                    using (CryptoStream cryptoStream = new CryptoStream(memoryStream, GetCryptoAlgorithm().CreateDecryptor(keyAndIvBytes, keyAndIvBytes), CryptoStreamMode.Read))
                    {
                        using (StreamReader srDecrypt = new StreamReader(cryptoStream))
                        {
                            plaintext = srDecrypt.ReadToEnd();
                        }
                    }
                }
            }
            catch (Exception e)
            {
                mException = e.Message;
            }
            return plaintext;
        }

        private static byte[] AesEncrypt(string inputText, Byte[] keyAndIvBytes)
        {
            byte[] inputBytes = UTF8Encoding.UTF8.GetBytes(inputText);
            byte[] result = null;
            using (MemoryStream memoryStream = new MemoryStream())
            {
                using (CryptoStream cryptoStream = new CryptoStream(memoryStream, GetCryptoAlgorithm().CreateEncryptor(keyAndIvBytes, keyAndIvBytes), CryptoStreamMode.Write))
                {
                    cryptoStream.Write(inputBytes, 0, inputBytes.Length);
                    cryptoStream.FlushFinalBlock();
                    result = memoryStream.ToArray();
                }
            }
            return result;
        }

        private static RijndaelManaged GetCryptoAlgorithm()
        {
            RijndaelManaged algorithm = new RijndaelManaged();
            //set the mode, padding and block size
            algorithm.Padding = PaddingMode.PKCS7;
            algorithm.Mode = CipherMode.CBC;
            algorithm.KeySize = 128;
            algorithm.BlockSize = 128;
            return algorithm;
        }
        #endregion
    }
}
