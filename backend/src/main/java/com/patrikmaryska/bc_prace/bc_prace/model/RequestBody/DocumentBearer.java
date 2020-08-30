package com.patrikmaryska.bc_prace.bc_prace.model.RequestBody;

import com.patrikmaryska.bc_prace.bc_prace.model.Document;

import java.util.List;

public class DocumentBearer {

    private List<Document> documentList;
    private long countOfRows;

    public DocumentBearer(){

    }

    public List<Document> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<Document> documentList) {
        this.documentList = documentList;
    }

    public long getCountOfRows() {
        return countOfRows;
    }

    public void setCountOfRows(long countOfRows) {
        this.countOfRows = countOfRows;
    }
}
