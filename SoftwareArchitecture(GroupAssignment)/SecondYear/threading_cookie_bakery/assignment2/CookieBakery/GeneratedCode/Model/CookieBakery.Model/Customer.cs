//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//------------------------------------------------------------------------------

using System;
using System.Threading;
using CookieBakery.GeneratedCode.Controller.CookieBakery.Core;

namespace CookieBakery.GeneratedCode.Model.CookieBakery.Model
{
    public class Customer
    {
        public string Name { get; private set; }

        public Customer(string name)
        {
            Name = name;

            Thread t = new Thread(Run);
            t.Name = name;
            t.Start();
        }

        private void Run()
        {
            // runs until store is closed
            while (!Bakery.GetInstance().IsClosed())
            {
                Thread.Sleep(1000);
                Bakery.GetInstance().SellCookieToCustomer(this);
            }

            Console.WriteLine(Name + " went home.");
            Console.ReadKey(); // to keep console open, so you can read input
        }
    }
}

