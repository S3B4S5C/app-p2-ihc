import { TestBed } from '@angular/core/testing';

import { CareRecord } from './care-record';

describe('CareRecord', () => {
  let service: CareRecord;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CareRecord);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
