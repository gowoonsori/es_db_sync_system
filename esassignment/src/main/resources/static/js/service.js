function onBulkEvent(serviceId) {
    const indexName = prompt("새로 bulk시 기존의 색인된 인덱스는 삭제됩니다.\n재색인을 원하시면 새로운 인덱스명을 입력해주십시오.")
    if (indexName === null) return;
    if (indexName.trim() === "") {
        alert("인덱스명을 입력하십시오.")
    }

    const button = document.getElementById(`button_${serviceId}`);
    button.disabled = true;
    const span = document.getElementById(`span_${serviceId}`);
    span.style.display = "none";
    const circular = document.getElementById(`circular_${serviceId}`);
    circular.style.display = "inline-block";

    fetch('/index/bulk', {
        method: 'post',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        body: JSON.stringify({
            'service_id': serviceId,
            'index_name': indexName
        }),
    })
        .then(res => {
            if (res.status === 200 || res.status === 201) {
                window.location.reload();
            } else {
                res.json().then(json => {
                    const message = json.message;
                    if (message instanceof Object) {
                        for (const property in message) {
                            alert(message[property]);
                        }
                    } else {
                        alert(message);
                    }
                });
                button.disabled = false;
                circular.style.display = "none";
                span.style.display = "inline-block";
            }
        }).catch(error => {
        button.disabled = false;
        circular.style.display = "none";
        span.style.display = "inline-block";
    });
}

const onCreateServiceEvent = () => {
    const serviceId = document.getElementById("service-id").value;
    const sqlString = document.getElementById("service-content").value;
    document.getElementById("valid_uuid").innerText = "";
    document.getElementById("valid_sqlString").innerText = "";

    fetch('/service/create', {
        method: 'post',
        headers: {
            'Content-Type': 'application/json;charset=UTF-8',
        },
        body: JSON.stringify({
            'service_id': serviceId,
            'sql_string': sqlString
        }),
    }).then(res => {
        if (res.status === 200 || res.status === 201) {
            window.location.href = "/service";
        } else {
            res.json().then(json => {
                const message = json.message;
                if (message instanceof Object) {
                    for (const property in message) {
                        document.getElementById(`${property}`).innerText = message[property];
                    }
                }else{
                    alert(message);
                }
            });
        }
    });
}

const onSearchEvent = () => {
    const serviceId = document.getElementById("service-id").value;
    const searchContent = document.getElementById("search-content").value;
    const searchField = document.getElementById("search-field").value.split(",").filter(field => field.trim() !== "").map(field => field.trim());
    const highlightField = document.getElementById("highlight-field").value.split(",").filter(field => field.trim() !== "").map(field => field.trim());
    const pageSize = document.getElementById("page-size").value;
    const pageNo = document.getElementById("page-no").value;

    if (serviceId.length < 1 || serviceId.length > 30) {
        alert("서비스 id는 1~30자로 입력해 주세요.");
    } else if (searchContent.length > 255) {
        alert("검색 키워드는 255자 내로 입력해주세요.");
    } else if (searchField.length > 0 && searchField.filter(field => !/^[a-zA-Z0-9_]*$/.test(field)).length > 0) {
        alert("잘못된 검색 필드 양식입니다.");
    } else if (highlightField.length > 0 && highlightField.filter(field => !/^[a-zA-Z0-9_]*$/.test(field)).length > 0) {
        alert("잘못된 하이라이트 필드 양식입니다.");
    } else if (pageSize !== "" && isNaN(parseInt(pageSize))) {
        alert("페이지 사이즈는 숫자만 입력이 가능합니다.");
    } else if (pageNo !== "" && isNaN(parseInt(pageNo))) {
        alert("페이지는 숫자만 입력이 가능합니다.");
    } else {
        const columns = highlightField.map(field => ({'column': field}));
        fetch('/index/search/contents', {
            method: 'post',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
            body: JSON.stringify({
                'service_id': serviceId,
                'search_text': searchContent,
                'result_columns': searchField,
                'highlight': {
                    'columns': columns,
                    'prefix_tag': '<b>',
                    'postfix_tag': '</b>'
                },
                'page_size': parseInt(pageSize),
                'page_no': parseInt(pageNo)
            }),
        }).then(async (res) => {
            if (res.status === 200) {
                const data = await res.json();
                makeTable(data.result);
            } else {
                res.json().then(json => {
                    const message = json.message;
                    if (message instanceof Object) {
                        for (const property in message) {
                            document.getElementById(`${property}`).innerText = message[property];
                        }
                    } else {
                        alert(message);
                    }
                });
            }
        })
    }
}

const makeTable = (docs) => {
    if (docs === null || docs === undefined) return;
    const thead = $("#result-table-head");
    const tbody = $("#result-table-body");
    $(".empty-span").remove();
    thead.empty();
    tbody.empty();
    if (docs?.length < 1) {
        $(".result-container").append('<h2 class="empty-span">검색 결과가 비었습니다.</h2>');
        return;
    }

    const rowCnt = docs.length;
    const columnCnt = Object.keys(docs[0]?.data).length;

    //thead
    let theadStr = '<tr><th></th>';
    for (let i = 0; i < columnCnt; i++) {
        theadStr += '<th class="center-align-column">';
        theadStr += Object.keys(docs[0]?.data)[i];
        theadStr += '</th>';
    }
    theadStr += '</tr>';
    thead.append(theadStr);

    //tbody
    for (let i = 0; i < rowCnt; i++) {
        const highlightFields = Object.keys(docs[i]?.highlight);
        console.log(highlightFields);
        let tbodyStr = `<tr><td>${i}</td>`

        for (let j = 0; j < columnCnt; j++) {
            const fieldName = Object.keys(docs[i]?.data)[j];
            const fieldValue = docs[i]?.data[Object.keys(docs[i]?.data)[j]];
            console.log(highlightFields.includes(fieldName));
            tbodyStr += '<td class="center-align-column">';
            if (highlightFields.includes(fieldName)) tbodyStr += docs[i]?.highlight[fieldName][0];
            else tbodyStr += fieldValue;
            theadStr += '</td>';
        }
        tbodyStr += '</tr>';
        tbody.append(tbodyStr);
    }
}

const onDeleteServiceEvent = (serviceId) => {
    const onDelete = confirm("삭제하실 경우 생성되었던 index는 삭제됩니다.\n정말로 삭제하시겠습니까? ");
    if (onDelete) {
        fetch(`/service/${serviceId}`, {
            method: 'delete',
            headers: {
                'Content-Type': 'application/json;charset=UTF-8',
            },
        }).then(async (res) => {
            if (res.status === 200) {
                window.location.reload();
            } else {
                const error = await res.json();
                if (error.message instanceof String) {
                    alert(error.message);
                }
            }
        });
    }
}