using CookieBakery.GeneratedCode.Model.CookieBakery.Model;
using CookieBakery.GeneratedCode.Model.CookieBakery.Model.Factory;
using NUnit.Core;
using NUnit.Framework;
using NUnit.Framework.Constraints;

namespace CookieBakery.Test.Model.CookieBakery.Model.Factory
{
    [TestFixture]
    internal class CookieFactoryTest
    {
        [Test]
        public void BakeCookieTest()
        {
            CookieFactory cookieFactory = new CookieFactory();
            Cookie cookie = cookieFactory.BakeCookie();
            Assert.IsNotNull(cookie);

            Assert.IsTrue(cookie.CookieType.GetType() == typeof (CookieFlyWeight.ChocolateCookie) ||
                          cookie.CookieType.GetType() == typeof (CookieFlyWeight.BlueberryCookie) ||
                          cookie.CookieType.GetType() == typeof (CookieFlyWeight.HotDogCookie));
        }
    }
}
