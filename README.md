# Json Mapper
Json Mapper is an object written in Scala that uses [Circe](https://circe.github.io/circe/) library

## Key concept
- **flattenDecoder()** Decoder that flattens Json. Output given is a Map where every key is a value from a different Json level
    ### Example
    #### Input
    ```Json
    {
        "name": "Pat",
        "metadata": [1,2,3],
        "metadata2": [
            {
                "name": "t1",
                "info": 256,
                "number": null  
            },
            {
                "name": "t3",
                "info": 133,
                "number": "3657483955"  
            }
        ]
    }
    ```

    #### Decoder Output (Json Format)
    ```Json
    {
        "name": "Pat",
        "metadata": [1,2,3],
        "metadata.0": 1,
        "metadata.1": 2,
        "metadata.2": 3,
        "metadata2": [
            {
                "name": "t1",
                "info": 256,
                "number": null
            },
            {
                "name": "t3",
                "info":133,
                "number": "3657483955"
            }
        ],
        "metadata2.0": {
            "name": "t1",
            "info": 256,
            "number": null
        },
        "metadata2.0.name": "t1",
        "metadata2.0.info": 256,
        "metadata2.0.number": null,
        "metadata2.1": {
            "name":"t3",
            "info": 133,
            "number":"3657483955"
        },
        "metadata2.1.name": "t3",
        "metadata2.1.info": 133,
        "metadata2.1.number":"3657483955"
    }
    ```
- **mapTemplate()**: Given a Json Template, map values if string has a mappingKeyword
    ### Example
    #### Input
    ```Json
    {
        "name": "MapTo.metadata2.0.name",
        "alternativeName": "MapTo.metadata2.1.name",
        "number": "MapTo.metadata2.0.number",
        "infos": [
        "MapTo.metadata2.0.info",
        "MapTo.metadata2.1.info" 
        ]
    }
    ```

    #### Output (Using Map Above)
    ```Json
    {
        "name": "t1",
        "alternativeName": "t3",
        "number": null,
        "infos": [256, 133]
    }
    ```
