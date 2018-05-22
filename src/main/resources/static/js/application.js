/**
 * Read Excel using JS-XLSX
 */

function handleFile(e, callback) {
    var files = e.target.files;
    var f = files[0];
    var reader = new FileReader();

    reader.onload = function (e) {
        var data = e.target.result;
        var workbook = XLSX.read(data, {type: 'binary'});
        var sheetNames = workbook.SheetNames;
        var worksheet = workbook.Sheets[sheetNames[0]];
        var json = XLSX.utils.sheet_to_json(worksheet);
        callback(json);

    };
    reader.readAsBinaryString(f);
}
