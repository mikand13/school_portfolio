//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//------------------------------------------------------------------------------

using CookieBakery.GeneratedCode.Controller.CookieBakery.Core;
using CookieBakery.GeneratedCode.Model.CookieBakery.Model.Factory;

namespace CookieBakery.GeneratedCode.Controller.CookieBakery.Driver
{
    public class Program
    {
        private static void Main()
        {
            Bakery.GetInstance();

            var c = new CustomerFactory();
            c.CreateCustomer("Fred");
            c.CreateCustomer("Greg");
            c.CreateCustomer("Ted");
        }
    }
}

