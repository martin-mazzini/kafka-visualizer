
console.log("Hi")
fetchConsumerData()

function fetchConsumerData() {
    console.log("fetch")
    return fetch("/consumer")
}
