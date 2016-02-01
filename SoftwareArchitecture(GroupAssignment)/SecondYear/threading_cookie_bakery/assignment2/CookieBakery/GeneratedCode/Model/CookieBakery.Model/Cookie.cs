//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//------------------------------------------------------------------------------

namespace CookieBakery.GeneratedCode.Model.CookieBakery.Model
{
    public class Cookie
    {
        public int Id { get; private set; }
        public CookieFlyWeight CookieType { get; private set; }

        public Cookie(int id, CookieFlyWeight cookieType)
        {
            Id = id;
            CookieType = cookieType;
        }
    }
}
