
console.log("Hi4")
fetchConsumerData()

function fetchConsumerData() {
    console.log("fetch")
    return fetch("/consumer")
        .then((response) => {
            if (response.ok) {   // *** This can be just `if (response.ok) {`
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
    console.log(data)
    data.forEach( consumer => {
        console.log(consumer)

    })

}
