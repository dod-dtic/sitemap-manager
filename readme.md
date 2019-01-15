# Sitemap Manager
This application was built to manage sitemaps, utilizing a sitemap index and child sitemap xml files.
## Build
1. navigate to project root directory, execute:
    ```
    build-dev.sh
    ```

## Debug
1. Install VS Code Extendsion: Microsoft - Debugger for Java
2. Run tomcat 8.5.* in debug mode with exposed port 8000 to attach
    ```
    $CATALINA_HOME/bin/catalina.sh jpda start
    ```
3. Deploy sitemap-manager.war to tomcat
4. Run VS Code debug

## Configuration
All configuration is stored in the application.properties file, utilized by the SitemapManagerConfiguration component

The configuration properties are listed and described here:
- `sitemap.manager.rootPath`
    - `string`: root directory to use for creating, reading, updating, deleting sitemap.xml and sitemap-*.xml files
- `sitemap.manager.rootPathWeb`
    - `string`: root web path to use for child sitemap-*.xml URLs
- `sitemap.manager.keyLength`
    - `int`: number of characters to use for the child sitemap "keys" (e.g., 5 characters for keys means url name "ABC1234567" is placed in sitemap-ABC12x.xml)
    - This value is configurable, but currently will require reprocessing of all included urls in order for the changes to take proper effect
    - This value is currently set to 5, which should strike an appropriate balance between number of sitemaps and the number of urls in the child urlsets
- `sitemap.manager.defaultPriority`
    - `double`: the default priority to use for urls that don't explicitly specify their own
- `sitemap.manager.defaultChangeFrequency`
    - `string`: the default change frequency to use for urls that don't explicitly specify their own. 
    - Should be one of: 
        - always
        - hourly
        - daily
        - weekly
        - monthly
        - yearly
        - never

## Usage
### Adding URLs (detailed)
To add URLs to the list of tracked URLs within the xml sitemaps, use a `POST` request against `/` with an object structure as described:

```json
{
    "urls": [
        {
            "location": "(required) the full URL",
            "name": "(optional) a name for the URL, which is used for the basis of the key for deciding in which sitemap-*.xml this url should reside. If none is provided, it will try to be inferred from the location value",
            "lastModified": "(optional) the last time this location was changed. If not provided, the application will assume the last time modified is the time of the current action being taken",
            "changeFrequency": "(optional) should be one of [always, hourly, daily, weekly, monthly, yearly, never]. If not provided, the application default will be used",
            "priority": "(optional) the relative priority of this location, expressed as a value between 0 and 1. If not provided, the application default will be used"
        }
    ]
}
```

Example:
```json
{
    "urls": [
        {
            "location": "https://apps.dtic.mil/docs/citations/AD0245212",
            "name": "AD0245212"
        },
        {
            "location": "https://apps.dtic.mil/docs/citations/AD1001121"
        },
        {
            "location": "https://apps.dtic.mil/docs/citations/AD1001411",
            "name": "AD1001411",
            "changeFrequency": "monthly",
            "priority": "0.9"
        }
    ]
}
```

If a POST request is received that contains locations that are already tracked, those locations will be treated as update requests.

### Adding URLs (simple/bulk)
To quickly add URLs with only default values to the list of tracked URLs within the xml sitemaps, use a `POST` request against `/simple` with a `text/plain` body consisting of a list of URLs (one per line):

Example:
```
http://www.dtic.mil/docs/citations/AD0037339
http://www.dtic.mil/docs/citations/AD1000195
http://www.dtic.mil/docs/citations/AD1000196
http://www.dtic.mil/docs/citations/AD1000263
http://www.dtic.mil/docs/citations/AD1000433
http://www.dtic.mil/docs/citations/AD1000436
http://www.dtic.mil/docs/citations/AD1000553
http://www.dtic.mil/docs/citations/AD1000556
http://www.dtic.mil/docs/citations/AD1000603
http://www.dtic.mil/docs/citations/AD1000610
http://www.dtic.mil/docs/citations/AD1000771
```

If a POST request is received that contains locations that are already tracked, those locations will be treated as update requests.

### Updating properties of existing URLs (detailed)
To update the properties of URLs that are already tracked within the xml sitemaps, use a `PUT` request against `/` with an object structure as described:

```json
{
    "urls": [
        {
            "location": "(required) the full URL",
            "name": "(optional) a name for the URL, which is used for the basis of the key for deciding in which sitemap-*.xml this url should reside. If none is provided, it will try to be inferred from the location value",
            "lastModified": "(optional) the last time this location was changed. If not provided, the application will assume the last time modified is the time of the current action being taken",
            "changeFrequency": "(optional) should be one of [always, hourly, daily, weekly, monthly, yearly, never]. If not provided, the application default will be used",
            "priority": "(optional) the relative priority of this location, expressed as a value between 0 and 1. If not provided, the application default will be used"
        }
    ]
}
```

Example:
```json
{
    "urls": [
        {
            "location": "https://apps.dtic.mil/docs/citations/AD1001411",
            "name": "AD1001411",
            "changeFrequency": "yearly",
            "priority": "0.5"
        }
    ]
}
```

If a PUT request is received that contains locations that are not already tracked, those locations will be treated as create requests.

### Updating URLs (simple/bulk)
To quickly change the last modified date of a list of URLs, use a `PUT` request against `/simple` with a `text/plain` body consisting of a list of URLs (one per line):

Example:
```
http://www.dtic.mil/docs/citations/AD0037339
http://www.dtic.mil/docs/citations/AD1000195
http://www.dtic.mil/docs/citations/AD1000196
http://www.dtic.mil/docs/citations/AD1000263
http://www.dtic.mil/docs/citations/AD1000433
http://www.dtic.mil/docs/citations/AD1000436
http://www.dtic.mil/docs/citations/AD1000553
http://www.dtic.mil/docs/citations/AD1000556
http://www.dtic.mil/docs/citations/AD1000603
http://www.dtic.mil/docs/citations/AD1000610
http://www.dtic.mil/docs/citations/AD1000771
```

If a PUT request is received that contains locations that are not already tracked, those locations will be treated as bulk create requests (utilizing default values for all fields other than the URL).

### Removing URLs from sitemaps (detailed)
To remove URLs that are already tracked within the xml sitemaps, use a `DELETE` request against `/` with an object structure as described:

```json
{
    "urls": [
        {
            "location": "(required) the full URL",
            "name": "(optional) a name for the URL, which is used for the basis of the key for deciding in which sitemap-*.xml this url should reside. If none is provided, it will try to be inferred from the location value"
        }
    ]
}
```
Example:
```json
{
    "urls": [
        {
            "location": "https://apps.dtic.mil/docs/citations/AD0245212",
            "name": "AD0245212"
        },
        {
            "location": "https://apps.dtic.mil/docs/citations/AD1001121"
        },
        {
            "location": "https://apps.dtic.mil/docs/citations/AD1001411",
            "name": "AD1001411"
        }
    ]
}
```

### Removing URLs  from sitemaps (simple/bulk)
To remove URLs that are already tracked within the xml sitemaps, use a `DELETE` request against `/simple` with a `text/plain` body consisting of a list of URLs (one per line):

Example:
```
http://www.dtic.mil/docs/citations/AD0037339
http://www.dtic.mil/docs/citations/AD1000195
http://www.dtic.mil/docs/citations/AD1000196
http://www.dtic.mil/docs/citations/AD1000263
http://www.dtic.mil/docs/citations/AD1000433
http://www.dtic.mil/docs/citations/AD1000436
http://www.dtic.mil/docs/citations/AD1000553
http://www.dtic.mil/docs/citations/AD1000556
http://www.dtic.mil/docs/citations/AD1000603
http://www.dtic.mil/docs/citations/AD1000610
http://www.dtic.mil/docs/citations/AD1000771
```