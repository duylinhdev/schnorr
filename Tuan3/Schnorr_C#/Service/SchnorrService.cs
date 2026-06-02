// Services/SchnorrService.cs
using System;
using System.Security.Cryptography;
using System.Text;
using System.Numerics;
using baitaplonmangmaytinh.Models;

namespace baitaplonmangmaytinh.Services
{
    public class SchnorrService
    {
        /// <summary>
        /// Tính base^exp mod mod dùng BigInteger
        /// </summary>
        public long ModPow(long baseVal, long exp, long mod)
        {
            if (mod <= 1) return 0;
            return (long)BigInteger.ModPow(
                new BigInteger(baseVal),
                new BigInteger(exp),
                new BigInteger(mod));
        }

        /// <summary>
        /// H(r || M) mod q
        /// Nối r và M thành chuỗi, băm SHA-256, lấy mod q
        /// </summary>
        public long ComputeHash(string message, long r, long q, string hashAlg = "SHA-256")
        {
            // Theo thuật toán: H(r || M) — nối r trước, M sau
            string input = $"{r}{message}";
            byte[] bytes = Encoding.UTF8.GetBytes(input);

            byte[] hashBytes = hashAlg == "SHA-512"
                ? SHA512.HashData(bytes)
                : SHA256.HashData(bytes);

            // Dùng BigInteger để tránh tràn số với hash lớn
            BigInteger bigVal = new BigInteger(hashBytes, isUnsigned: true, isBigEndian: true);
            return (long)(bigVal % (BigInteger)q);
        }

        /// <summary>
        /// Sinh khóa công khai: y = g^x mod p
        /// </summary>
        public long GeneratePublicKey(long g, long x, long p)
        {
            return ModPow(g, x, p);
        }

        /// <summary>
        /// Thuật toán Ký (Signing Algorithm):
        ///   Bước 1: Chọn nonce k bí mật: 1 ≤ k ≤ q-1
        ///   Bước 2: Tính giá trị cam kết: r = g^k mod p
        ///   Bước 3: Tính giá trị băm:     e = H(r || M)
        ///   Bước 4: Tính giá trị phản hồi: s = (k - x·e) mod q
        ///   Chữ ký số: cặp (e, s) hoặc (s, e)
        /// </summary>
        public SignatureResult Sign(
            SystemParameters param,
            long x,
            long k,
            string message,
            string hashAlg = "SHA-256")
        {
            // Bước 2: r = g^k mod p
            long r = ModPow(param.G, k, param.P);

            // Bước 3: e = H(r || M) mod q
            long e = ComputeHash(message, r, param.Q, hashAlg);

            // Bước 4: s = (k - x*e) mod q  — đảm bảo kết quả không âm
            BigInteger bigS =
                (((BigInteger)k - (BigInteger)x * e) % param.Q + param.Q)
                % param.Q;

            return new SignatureResult
            {
                R = r,
                E = e,
                S = (long)bigS
            };
        }

        /// <summary>
        /// Thuật toán Xác Minh (Verification Algorithm):
        ///   Bước 1: Tính giá trị khôi phục: r' = g^s · y^e mod p
        ///   Bước 2: Tính e' = H(r' || M)
        ///   Bước 3: Kiểm tra: Hợp lệ nếu e' = e
        /// </summary>
        public VerificationResult Verify(
            SystemParameters param,
            long y,
            string message,
            long e,
            long s,
            string hashAlg = "SHA-256")
        {
            // g^s mod p
            BigInteger gs = BigInteger.ModPow(param.G, s, param.P);

            // y^e mod p
            BigInteger ye = BigInteger.ModPow(y, e, param.P);

            // r' = g^s · y^e mod p
            long rPrime = (long)((gs * ye) % param.P);

            // e' = H(r' || M) mod q
            long ePrime = ComputeHash(message, rPrime, param.Q, hashAlg);

            return new VerificationResult
            {
                RPrime = rPrime,
                EPrime = ePrime,
                IsValid = (ePrime == e)
            };
        }
    }
}