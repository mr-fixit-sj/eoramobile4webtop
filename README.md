# Eora Mobile4Webtop

Mobile Interface Component for EMC Documentum Webtop ©. This component will instantly add mobile support for your existing Webtop installation. Based on jQuery Mobile 1.4.5 it will support most mobile devices. 

## Features

- [x] Repository browsing
- [x] View object properties
- [x] View object locations
- [x] View object versions
- [x] View,add or remove subscriptions
- [x] Send DRL links with mailto:... link
- [x] View content via ACS or wdk5 downloadservlet link
- [x] Search (fullcontent and/or metadata)
- [x] Define object properties display layout lay via xml configuration or docbase display configurations
- [x] Mobile friendly DRL page
- [x] Add custom attribute value formatters
- [ ] Import Content (beta)	
- [ ] Edit metadata, full support for existing value assistance	(beta)


## Build

Eora Mobile4Webtop is a project based on **Apache Maven**. Since the required EMC Documentum libraries are proprietary you need to add them to your local maven repository.

**Required Documentum libraries**
- Webtop classes
- subscription.jar
- dfc.jar
- DmcRecords.jar

For webtop.jar, zip all classfiles from the default Webtop installation ```webtop\WEB-INF\classes``` and rename to webtop.jar. Other jars are found in: ```webtop\WEB-INF\lib```

Depending on the versions you use correct **-Dversion** in the following statements **and in your pom.xml** if necessary.

```
mvn install:install-file -Dfile=./dfc.jar -DgroupId=com.documentum -DartifactId=dfc -Dversion=7.1.0100.0155 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=./DmcRecords.jar -DgroupId=com.documentum -DartifactId=drs -Dversion=7.1.0100.0166 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=./subscription.jar -DgroupId=com.documentum -DartifactId=subscriptions -Dversion=7.1.0100.0166 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=./webtop_classes.jar -DgroupId=com.documentum -DartifactId=webtop -Dversion=6.8.0000.0286 -Dpackaging=jar -DgeneratePom=true

cd mobile4webtop
mvn clean package
```

This produces a WAR file ```mobile4webtop.war``` containing all required files for installation. 

## Install

**Add application layer**
The mobile4webtop adds a extra application scope named 'eora' to the webtop. The top (default = 'custom') application layer needs to extend from this 'eora' layer. 

If you do not have any changes in the default file ```webtop\custom\app.xml```, you can use the supplied ```custom\app.xml``` from the WAR or edit your existing file ```webtop\custom\app.xml```

```
<config>
	<scope>
		<!-- Mobile4Webtop can be integrated by adding the eora application scope -->
		<application extends="eora/app.xml">
		....
		....
```

**Extract mobile4webtop.war archive into webtop installation**

From the archive extract the ```eora``` folder into your webtop installation. After extraction your webtop installation folder structure should look like:

```
custom
eora
wdk
webcomponent
webtop
```

Now startup your webtop installation and connect to it with a mobile device, or use a useragent switcher extension for your browser to test.

## Components

The section introduces some of the details of the key components the project relies on.

- [MobileRepositoryBrowser](docs/components/mobilereposbrowser.md)
- [DRL](docs/components/drl.md)
- [Login](docs/components/login.md)
- [Logoff](docs/components/logoff.md)
- [Prompt](docs/components/prompt.md)
- [Main](docs/components/main.md)
- [Import](docs/components/import.md)


## Guides

- [How to ...?](docs/guides/howto.md)
- TODO

## License

Eora Mobile4Webtop project is under MIT License.
