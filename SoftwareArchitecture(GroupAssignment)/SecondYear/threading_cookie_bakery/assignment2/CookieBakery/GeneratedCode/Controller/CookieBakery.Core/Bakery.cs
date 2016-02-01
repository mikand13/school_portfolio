//------------------------------------------------------------------------------
// @author Anders Mikkelsen - mikand13@student.westerdals.no
// @author Espen Rønning - ronesp13@student.westerdals.no
// @author Pia Dokken Stranger-Johannessen - strpia13@student.westerdals.no
//-------------------------------------------------------------------------------

using System;
using System.Collections.Generic;
using System.Threading;
using CookieBakery.GeneratedCode.Model.CookieBakery.Model;
using CookieBakery.GeneratedCode.Model.CookieBakery.Model.Factory;

namespace CookieBakery.GeneratedCode.Controller.CookieBakery.Core
{
    public sealed class Bakery
    {
        private readonly Object thisLock = new Object();

        private const int MinCookiesPerDay = 12;
        private const int MaxCookiesPerDay = 30;
        private int CookiesRemaining { get; set; }

        private CookieFactory CookieFactory { get; set; }
        private List<Cookie> CookieCounter { get; set; }

        public Bakery()
        {
            CookieCounter = new List<Cookie>();
            CookieFactory = new CookieFactory();

            CookiesRemaining = SetDailyCookies();            

            new Thread(Run).Start();
        }

        private int SetDailyCookies()
        {
            return new Random().Next(MinCookiesPerDay, MaxCookiesPerDay);
        }

        public void Run()
        {
            // run until all daily cookies have been baked and sold
            while (CookiesRemaining > 0)
            {
                Thread.Sleep(667);
                OrderCookie();
            }
        }

        private void OrderCookie()
        {
            var cookie = CookieFactory.BakeCookie();

            CookieCounter.Add(cookie);
            CookiesRemaining--;

            Console.WriteLine("Bakery made cookie #" + cookie.Id);
        }

        public bool IsClosed()
        {
            return CookiesRemaining <= 0 && CookieCounter.Count == 0;
        }

        public void SellCookieToCustomer(Customer customer)
        {
            Cookie cookie;

            lock (thisLock)
            {
                // deny customer if store empty
                if (CookieCounter.Count == 0)
                {
                    return;
                }

                cookie = CookieCounter[0];
                CookieCounter.RemoveAt(0);
            }

            Console.WriteLine("\t\t\t\t\t" + customer.Name + " received cookie #" + cookie.Id);
        }      

        public static Bakery GetInstance()
        {
            return SingletonHolder.Instance;
        }

        private class SingletonHolder
        {
            public static readonly Bakery Instance = new Bakery();
        }
    }
}

