
console.log("Hi4")
shortPollForConsumerData()

function fetchConsumerData() {
    return fetch("/consumer")
        .then((response) => {
            if (response.ok) {
                return response.json();
            }
            else {
                throw `error with status ${response.status}`;
            }
        })
        .then(json => renderConsumerData(json))
        .catch(
            error => {
                console.log("Error fetch consumer data: " + error)
            }
        )
}

function renderConsumerData(data) {

    const consumerTables = document.querySelectorAll(".data_table")
    console.log(consumerTables)

    const consumerDataMap = data.reduce(function(map, consumer) {
        map[consumer.consumerId] = consumer;
        return map;
    }, {});
    console.log(consumerDataMap)



    consumerTables.forEach( consumerTable => {

        var consumerData = consumerDataMap[consumerTable.id]
        console.log(consumerData)

        if (consumerData == null){
            console.log("inactive")
        }else{
            const p = consumerTable
                .querySelector('.records_divs')
                .querySelector('p')
            let recordList = ""
            for (let record of consumerData.records) {
                recordList = recordList + record +  " <br/> "
            }
            p.innerHTML = recordList
        }
    })


/*
    data.forEach( consumer => {

        const p = document.querySelector("#" + consumer.consumerId)
            .querySelector('.records_divs')
            .querySelector('p')

        var recordList = ""
        for (let record of consumer.records) {
            recordList = recordList + record +  " <br/> "
        }

        p.innerHTML = recordList

    })*/
}


function shortPollForConsumerData() {
    const interval = setInterval(function () {
        fetchConsumerData()
    }, 2000);

}
