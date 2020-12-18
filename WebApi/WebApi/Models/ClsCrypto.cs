using System;
using System.Linq;
using System.Text;
using System.IO;

using System.Security.Cryptography;

namespace WebApi.Models
{
    public class ClsCrypto
    {
        private RijndaelManaged myRijndael = new RijndaelManaged();
        private int iterations;
        private byte[] salt;

        public ClsCrypto(string strPassword)
        {
            myRijndael.BlockSize = 128;
            myRijndael.KeySize = 128;
            myRijndael.IV = HexStringToByteArray("a5s8d2e9c1721ae0e84ad660c472y1f3");
            //myRijndael.IV = HexStringToByteArray("00000000000000000000000000000000");

            myRijndael.Padding = PaddingMode.PKCS7;
            myRijndael.Mode = CipherMode.CBC;
            iterations = 1000;
            salt = System.Text.Encoding.UTF8.GetBytes("cryptography123example");
            myRijndael.Key = GenerateKey(strPassword);
        }

        public string Encrypt(string strPlainText)
        {
            byte[] strText = new System.Text.UTF8Encoding().GetBytes(strPlainText);
            ICryptoTransform transform = myRijndael.CreateEncryptor();
            byte[] cipherText = transform.TransformFinalBlock(strText, 0, strText.Length);
            return Convert.ToBase64String(cipherText);
        }

        public string Decrypt(string encryptedText)
        {
            var encryptedBytes = Convert.FromBase64String(encryptedText);
            ICryptoTransform transform = myRijndael.CreateDecryptor();
            byte[] cipherText = transform.TransformFinalBlock(encryptedBytes, 0, encryptedBytes.Length);
            return System.Text.Encoding.UTF8.GetString(cipherText);
        }

        public static byte[] HexStringToByteArray(string strHex)
        {
            dynamic r = new byte[strHex.Length / 2];
            for (int i = 0; i <= strHex.Length - 1; i += 2)
            {
                r[i / 2] = Convert.ToByte(Convert.ToInt32(strHex.Substring(i, 2), 16));
            }
            return r;
        }

        private byte[] GenerateKey(string strPassword)
        {
            Rfc2898DeriveBytes rfc2898 = new Rfc2898DeriveBytes(System.Text.Encoding.UTF8.GetBytes(strPassword), salt, iterations);
            return rfc2898.GetBytes(128 / 8);
        }
    }
}
