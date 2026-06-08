// wwwroot/js/schnorr.js

function getParams() {
    return {
        P: parseInt(document.getElementById('P').value),
        Q: parseInt(document.getElementById('Q').value),
        G: parseInt(document.getElementById('G').value)
    };
}

function getHashAlg() {
    return document.getElementById('HashAlg').value;
}

async function callApi(url, data) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    });
    return await res.json();
}

async function generateKey() {
    const params = getParams();
    const x = parseInt(document.getElementById('X').value);
    const result = await callApi('/Schnorr/GenerateKey', { ...params, X: x });

    document.getElementById('publicKeyDisplay').textContent = 'y = ' + result.y;
    document.getElementById('publicKeyFormula').textContent = result.formula;
    // Lưu Y vào hidden input để dùng cho bước xác thực
    document.getElementById('Y_hidden').value = result.y;
}

async function signMessage() {
    const params = getParams();
    const x = parseInt(document.getElementById('X').value);
    const k = parseInt(document.getElementById('K').value);
    const msg = document.getElementById('Message').value;
    const hashAlg = getHashAlg();

    const result = await callApi('/Schnorr/Sign', {
        ...params, X: x, K: k, Message: msg, HashAlg: hashAlg
    });

    document.getElementById('R_val').textContent = result.r;
    document.getElementById('E_val').textContent = result.e;
    document.getElementById('sig_e').textContent = result.e;
    document.getElementById('sig_s').textContent = result.s;

    // Tự điền sang bước 4
    document.getElementById('VerifyMsg').value = msg;
    document.getElementById('VerifyE').value = result.e;
    document.getElementById('VerifyS').value = result.s;
}

async function verifySignature() {
    const params = getParams();
    const y = parseInt(document.getElementById('Y_hidden').value);
    const msg = document.getElementById('VerifyMsg').value;
    const e = parseInt(document.getElementById('VerifyE').value);
    const s = parseInt(document.getElementById('VerifyS').value);
    const hashAlg = getHashAlg();

    const result = await callApi('/Schnorr/Verify', {
        ...params, Y: y, Message: msg, E: e, S: s, HashAlg: hashAlg
    });

    document.getElementById('rPrime_val').textContent = result.rPrime;
    document.getElementById('ePrime_val').textContent = result.ePrime;

    const div = document.getElementById('verifyResult');
    if (result.isValid) {
        div.innerHTML = `
            <div class="result-valid">
                <div class="icon">✅</div>
                <div class="label">HỢP LỆ</div>
                <div class="sublbl">SIGNATURE VERIFIED SUCCESSFULLY</div>
            </div>`;
    } else {
        div.innerHTML = `
            <div class="result-invalid">
                <div class="icon">❌</div>
                <div class="label">KHÔNG HỢP LỆ</div>
                <div class="sublbl">SIGNATURE VERIFICATION FAILED</div>
            </div>`;
    }
}

function resetForm() {
    location.reload();
}