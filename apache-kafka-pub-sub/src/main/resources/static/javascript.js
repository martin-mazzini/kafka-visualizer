console.log("Hi")
shortPollForConsumerData()
shortPollForProducerData()

function fetchConsumerData() {
    return fetch("/consumer")
        .then((response) => {
            if (response.ok) {
                return response.json();
            } else {
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


function fetchProducerData() {
    return fetch("/producer")
        .then((response) => {
            if (response.ok) {
                return response.json();
            } else {
                throw `error with status ${response.status}`;
            }
        })
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

            if (!consumerTable.classList.contains("inactive")) {

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


        } else {
            const p = consumerTable
                .querySelector('.records_divs')
                .querySelector('p')
            let recordList = ""
            for (let record of consumerData.records) {
                recordList = recordList + record + " <br/> "
            }
            p.innerHTML = recordList


            const partitions = consumerTable
                .querySelector('.partitions_div')
            partitions.innerHTML = consumerData.partitions


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
            }


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
    console.log("rendering data: " + producerData)
    const producerTable = document.querySelector("#producer")
    const p = producerTable
        .querySelector('.records_divs')
        .querySelector('p')
    let recordList = ""

    //single producer at the moment
    let oneProducerData = producerData[0]
    for (let record of oneProducerData.records) {
        recordList = recordList + record + " <br/> "
    }
    p.innerHTML = recordList
}



