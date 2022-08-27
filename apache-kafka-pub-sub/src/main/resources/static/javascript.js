console.log("Hi")

window.onload = setUp;


function setUp() {
    shortPollForConsumerData()
    shortPollForProducerData()
    shortPollForPartitionsData()
    setProducerDataOnLoad()


}


function doFetchConsumerData() {
    return fetch("/consumer")
        .then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                throw `error with status ${response.status}`;
            }
        });
}

function fetchConsumerData() {
    return doFetchConsumerData()
        .then(json => renderConsumerData(json))
        .catch(
            error => {
                console.log("Error fetch consumer data: " + error)
            }
        )
}


function doFetchProducerData() {
    return fetch("/producer")
        .then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                throw `error with status ${response.status}`;
            }
        });
}

function fetchProducerData() {
    return doFetchProducerData()
        .then(json => renderProducerData(json))
        .catch(
            error => {
                console.log("Error fetch producer data: " + error)
            }
        )
}

function addConsumer(consumerId) {
    console.log("adding consumerId " + consumerId)
    return fetch("/consumer/" + consumerId,
        {
            method: 'PUT',
        }
    )
}

function removeConsumer(consumerId) {
    console.log("removing consumerId " + consumerId)
    return fetch("/consumer/" + consumerId,
        {
            method: 'DELETE',
        }
    )
}


function updateConsumer(id, latency) {

    console.log("Updating latency for consumer: " + latency + " -" + id)
    return fetch(`/consumer/` + id,
        {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({latency: latency})
        });

}

function renderActiveConsumerTable(consumerTable, consumerData, button) {
    const p = consumerTable
        .querySelector('.records_divs')
        .querySelector('p')
    let recordList = ""
    for (let record of consumerData.records) {
        recordList = recordList + record.offset + " | " + record.word +  " <br/> "
    }
    p.innerHTML = recordList


    const partitions = consumerTable
        .querySelector('.partitions_div')
    partitions.innerHTML = consumerData.partitions


    consumerTable.querySelector('.group_div').innerHTML = consumerData.consumerGroup


    //quick way to do execute this block only once when state changes from inactive to active
    if (!consumerTable.classList.contains("active")) {
        consumerTable.classList.remove("inactive")
        consumerTable.classList.add("active")

        button.innerHTML = "Remove consumer"
        button.classList.remove("add_button")
        button.classList.add("delete_button")


        let cloneButton = removeListeners(button)
        cloneButton.addEventListener("click", function () {
            removeConsumer(consumerTable.id)
        })


        let latencyInput = consumerTable.querySelector('.latency_checkbox')
        latencyInput.value = consumerData.latency
        latencyInput.disabled = false
        latencyInput.addEventListener('change', (event) => {
            updateConsumer(consumerTable.id, event.currentTarget.value)
        })

    }
}

function renderInactiveConsumerTable(consumerTable, button) {
    //quick way to do execute this block only once when state changes from active to inactive
    if (!consumerTable.classList.contains("inactive")) {



        consumerTable.querySelector('.partitions_div').innerHTML = ""
        consumerTable.querySelector('.group_div').innerHTML = ""
        consumerTable.querySelector('.latency_checkbox').disabled = true
        consumerTable.querySelector('.latency_checkbox').value = null

        consumerTable.classList.add("inactive")
        consumerTable.classList.remove("active")

        button.innerHTML = "Add consumer"
        button.classList.add("add_button")
        button.classList.remove("delete_button")

        let cloneButton = removeListeners(button)
        cloneButton.addEventListener("click", function () {
            addConsumer(consumerTable.id)
        })


    }
}

function renderConsumerData(data) {

    const consumerTables = document.querySelectorAll(".data_table")


    const consumerDataMap = data.reduce(function (map, consumer) {
        map[consumer.consumerId] = consumer;
        return map;
    }, {});


    consumerTables.forEach(consumerTable => {

        var consumerData = consumerDataMap[consumerTable.id]
        const button = consumerTable.querySelector('.change_button')
        // console.log("Processing: " + consumerTable.id)

        if (consumerData == null) {
            renderInactiveConsumerTable(consumerTable, button);
        } else {
            renderActiveConsumerTable(consumerTable, consumerData, button);
        }
    })
}


function removeListeners(oldBtnElement) {
    const newBtnElement = oldBtnElement.cloneNode(true);
    oldBtnElement.parentNode.replaceChild(newBtnElement, oldBtnElement);
    console.log("Removed all listners")
    return newBtnElement;
}


function shortPollForConsumerData() {
    const interval = setInterval(function () {
        fetchConsumerData()
    }, 200);

}


function shortPollForProducerData() {
    const interval = setInterval(function () {
        fetchProducerData()
    }, 200);

}


function renderProducerData(producerData) {


    let oneProducerData = producerData[0]
    const producerTable = document.querySelector("#producer")


    const p = producerTable
        .querySelector('.records_divs')
        .querySelector('p')
    let recordList = ""

    //single producer at the moment

    for (let record of oneProducerData.records) {
        recordList = recordList + record + " <br/> "
    }
    p.innerHTML = recordList
}

//`https://api.parse.com/1/users?foo=${encodeURIComponent(data.foo)}&bar=${encodeURIComponent(data.bar)}`


function updateUseKey(useKey, latency) {
    return fetch(`/producer`,
        {
            method: 'PATCH',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({latency: latency, useKey: useKey})
        });
}

function setProducerDataOnLoad() {

    return doFetchProducerData()
        .then(json => {
            const producerTable = document.querySelector("#producer")
            let oneProducerData = json[0]
            let checkBoxInput = producerTable.querySelector('.key_checkbox');
            let latencyInput = producerTable.querySelector('.latency_checkbox');



            latencyInput.value = oneProducerData.latency;
            latencyInput.addEventListener('change', (event) => {
                console.log("Updating latency: " + event.currentTarget.value)
                updateUseKey(null, event.currentTarget.value)
            })


            checkBoxInput.checked = oneProducerData.useKey;
            checkBoxInput.addEventListener('change', (event) => {
                console.log("Updating use key: " + event.currentTarget.checked)
                updateUseKey(event.currentTarget.checked, null)
            })

        })
        .catch(
            error => {
                console.log("Error fetch producer data: " + error)
            }
        )
}


function renderPartitionsData(json) {

    const table = document.querySelector(".topics")

    for (var i = 2, row; row = table.rows[i]; i++) {
        let partitionData = json[i-2]
        table.rows[i].cells[0].innerHTML =  partitionData.partition
        table.rows[i].cells[1].innerHTML =  partitionData.endOffset
        table.rows[i].cells[2].innerHTML =  partitionData.current
        table.rows[i].cells[3].innerHTML  = partitionData.lag
    }

}

function fetchPartitionData() {
    fetch("/topicpartitions")
        .then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                throw `error with status ${response.status}`;
            }
        })
        .then(json => renderPartitionsData(json))
        .catch(
            error => {
                console.log("Error fetch consumer data: " + error)
            }
        )
}

function shortPollForPartitionsData() {
    const interval = setInterval(function () {
        fetchPartitionData();
    }, 200);
}

