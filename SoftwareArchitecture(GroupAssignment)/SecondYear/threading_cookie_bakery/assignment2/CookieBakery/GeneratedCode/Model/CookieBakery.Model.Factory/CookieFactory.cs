//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//------------------------------------------------------------------------------

using System;
using System.Collections.Generic;

namespace CookieBakery.GeneratedCode.Model.CookieBakery.Model.Factory
{
    public class CookieFactory
    {
        private readonly Dictionary<ECookieType, CookieFlyWeight> activeCookies;

        private int CookiesBaked { get; set; }

        public CookieFactory()
        {
             activeCookies = new Dictionary<ECookieType, CookieFlyWeight>();
        }

        public Cookie BakeCookie()
        {
            var cookieToBake = (ECookieType) new Random().Next(0, Enum.GetNames(typeof (ECookieType)).Length);

            if (activeCookies.ContainsKey(cookieToBake))
            {
                return new Cookie(CookiesBaked++, activeCookies[cookieToBake]);
            }

            switch (cookieToBake)
            {
                case ECookieType.ChocolateCookie:
                    activeCookies.Add(cookieToBake, new CookieFlyWeight.ChocolateCookie());
                    return new Cookie(CookiesBaked++, activeCookies[cookieToBake]);
                case ECookieType.BlueberryCookie:
                    activeCookies.Add(cookieToBake, new CookieFlyWeight.BlueberryCookie());
                    return new Cookie(CookiesBaked++, activeCookies[cookieToBake]);
                case ECookieType.HotDogCookie:
                    activeCookies.Add(cookieToBake, new CookieFlyWeight.HotDogCookie());
                    return new Cookie(CookiesBaked++, activeCookies[cookieToBake]);
            }
            return null;
        }
    }
}

