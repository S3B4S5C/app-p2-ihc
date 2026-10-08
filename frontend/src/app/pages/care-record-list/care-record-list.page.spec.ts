import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CareRecordListPage } from './care-record-list.page';

describe('CareRecordListPage', () => {
  let component: CareRecordListPage;
  let fixture: ComponentFixture<CareRecordListPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CareRecordListPage],
    }).compileComponents();

    fixture = TestBed.createComponent(CareRecordListPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
