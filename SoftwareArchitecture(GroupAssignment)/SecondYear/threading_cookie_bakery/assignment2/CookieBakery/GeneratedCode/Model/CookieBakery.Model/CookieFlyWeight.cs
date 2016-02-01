//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//------------------------------------------------------------------------------
namespace CookieBakery.GeneratedCode.Model.CookieBakery.Model 
{
    public abstract class CookieFlyWeight 
    {
        // placement variables for expansion
        private int Width { get; set; }
        private int Height { get; set; }
        private string Flavour { get; set; }

        protected CookieFlyWeight() 
        {
            // standard size for all cookies
            Width = 10;
            Height = 12;
        }

        public class ChocolateCookie : CookieFlyWeight 
        {
            public ChocolateCookie() 
            {
                Flavour = "Chocolate";
            }
        }

        public class BlueberryCookie : CookieFlyWeight 
        {
            public BlueberryCookie() 
            {
                Flavour = "Blueberry";
            }
        }

        public class HotDogCookie : CookieFlyWeight 
        {
            public HotDogCookie() 
            {
                Flavour = "HotDogCookie";
            }
        }
    }
}

