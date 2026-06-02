// Controllers/SchnorrController.cs
using Microsoft.AspNetCore.Mvc;
using baitaplonmangmaytinh.Models;
using baitaplonmangmaytinh.Services;

namespace baitaplonmangmaytinh.Controllers
{
    public class SchnorrController : Controller
    {
        private readonly SchnorrService _svc;

        public SchnorrController(SchnorrService svc)
        {
            _svc = svc;
        }

        public IActionResult Index() => View();

        [HttpPost]
        public IActionResult GenerateKey([FromBody] GenerateKeyRequest req)
        {
            long y = _svc.GeneratePublicKey(req.G, req.X, req.P);
            return Json(new
            {
                y,
                formula = $"y = {req.G}^{req.X} mod {req.P} = {y}"
            });
        }

        [HttpPost]
        public IActionResult Sign([FromBody] SignRequest req)
        {
            var param = new SystemParameters { P = req.P, Q = req.Q, G = req.G };
            var result = _svc.Sign(param, req.X, req.K, req.Message, req.HashAlg);
            return Json(result);
        }

        [HttpPost]
        public IActionResult Verify([FromBody] VerifyRequest req)
        {
            var param = new SystemParameters { P = req.P, Q = req.Q, G = req.G };
            var result = _svc.Verify(param, req.Y, req.Message, req.E, req.S, req.HashAlg);
            return Json(result);
        }
    }
}