# [Ekrequilt](https://ekrequilt.herokuapp.com/)

Gruppe 3 PG4300-14

* Anders Mikkelsen
* Espen Rønning
* Pia Dokken Stranger-Johannessen

## Oppgavebeskrivelse

Applikasjonen skal være en nettbutikk som selger diverse quilteprodukter, hvor kundes kunder skal kunne logge inn og bestille varer. Butikkeier skal kunne administrere butikken og leverandører skal kunne selge sine produkter via kunden.

## Current

* Current production build can be tried out at https://ekrequilt.herokuapp.com/
* Du kan logge inn med facebook / gmail. Hvis du ikke ønsker det kan du bruker følgende testbrukere:
  * mikand13@ekrequilt.no /// dev
  * strpia13@ekrequilt.no /// dev
  * ronesp13@ekrequilt.no /// dev

* Hvis du vil sjekke ut foreløpig admin / butikksjef funksjonalitet kan du logge inn med:
  * admin@ekrequilt.no /// dev
  * store_manager@ekrequilt.no /// dev
  
  på: https://ekrequilt.herokuapp.com/admin_accounts/sign_in

* Paypal
  * Paypal kan betales med Express eller vanlig Paypal. (Foreløpig test bruker /// testbutikk)
    * Selger: mikrondok-facilitator@gmail.com /// Hemmelig1
    * Buyer: mikrondok-buyer@gmail.com /// Hemmelig1
  
  * Kreditkort-betaling fungerer _midlertidig_ ikke grunnet en intern feil hos Paypal (06.06 21:15)
  * Vanlig paypal må fortsatt ha ett legitimt CC-nummer og verifiseringsnummer. Du kan bruke:
    * 4024007148673576
    * 123
  
  
## Prerequisites
1. Ruby 2.2.2
2. Rails 4.2.1

## Development

* Run:

```
bundle install
```

* If you do not have PostgreSQL installed locally you can run the following to avoid problems

```
bundle install --without production
```

* Access the app on heroku with following login:
  * User: mikrondok@gmail.com
  * Pass: Hemmelig123

* Run: (this will download necessary environment variables from heroku)

```
rake config:pull
```

* Migrate: (might need to set execute privledges)

```
./reset-db
```

* Start server:

```
rails s
```

## Deployment to heroku

* Run: (if you have added any information to .env)

```
rake config:push
```

* Run:

```
git push heroku master
```

* Run: (resets database)

```
heroku pg:reset DATABASE --confirm ekrequilt
```

* Run: (migrates and seed database, db:migrate if production)

```
heroku run rake db:setup
```

* Verify: (Opens browser window)

```
heroku open
```

## Testing

* Migrate: (might need to set execute privledges)

```
./reset-db-test
```

* Run (unit and functional tests):

```
rake test
```

* Run: (cucumber tests)

```
cucumber
```

* Run: (all tests, includes rake stats)

```
./all-test
```


## Kjente problemer og annen dokumentasjon

 * Finnes i Gruppedokument i rotmappen.







