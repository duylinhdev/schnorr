// Models/SchnorrModels.cs
namespace baitaplonmangmaytinh.Models
{
    public class SystemParameters
    {
        public long P { get; set; } = 48731;
        public long Q { get; set; } = 443;
        public long G { get; set; } = 11444;
    }

    public class SignatureResult
    {
        public long R { get; set; }
        public long E { get; set; }
        public long S { get; set; }
    }

    public class VerificationResult
    {
        public bool IsValid { get; set; }
        public long RPrime { get; set; }
        public long EPrime { get; set; }
    }

    public class GenerateKeyRequest
    {
        public long P { get; set; }
        public long Q { get; set; }
        public long G { get; set; }
        public long X { get; set; }
    }

    public class SignRequest
    {
        public long P { get; set; }
        public long Q { get; set; }
        public long G { get; set; }
        public long X { get; set; }
        public long K { get; set; }
        public string Message { get; set; } = "";
        public string HashAlg { get; set; } = "SHA-256";   // ← mới
    }

    public class VerifyRequest
    {
        public long P { get; set; }
        public long Q { get; set; }
        public long G { get; set; }
        public long Y { get; set; }
        public long E { get; set; }
        public long S { get; set; }
        public string Message { get; set; } = "";
        public string HashAlg { get; set; } = "SHA-256";   // ← mới
    }
}