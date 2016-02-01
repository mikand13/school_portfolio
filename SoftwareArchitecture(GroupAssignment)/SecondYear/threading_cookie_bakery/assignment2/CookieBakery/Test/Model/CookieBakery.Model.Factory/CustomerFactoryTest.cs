using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using CookieBakery.GeneratedCode.Model.CookieBakery.Model.Factory;
using NUnit.Framework;

namespace CookieBakery.Test.Model.CookieBakery.Model.Factory {
    [TestFixture]
    class CustomerFactoryTest {
        [Test]
        public void CreateCustomerTest() {
            ProcessThreadCollection currentThreads = Process.GetCurrentProcess().Threads;

            var beforeStartingThreads = currentThreads.Count;

            CustomerFactory customerFactory = new CustomerFactory();
            customerFactory.CreateCustomer("Fred");
            customerFactory.CreateCustomer("Greg");
            customerFactory.CreateCustomer("Ted");

            var afterStartingThreads = currentThreads.Count;

            Assert.AreEqual(beforeStartingThreads, afterStartingThreads);
        }
    }
}
