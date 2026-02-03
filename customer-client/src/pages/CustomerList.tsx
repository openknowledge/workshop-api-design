/*
 * Copyright 2026 open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { useNavigate } from 'react-router-dom';
import {
  useReactTable,
  getCoreRowModel,
  getSortedRowModel,
  flexRender,
  createColumnHelper,
  type SortingState,
} from '@tanstack/react-table';
import { useState } from 'react';
import { useCustomers } from '../api/hooks';
import type { Customer } from '../types/customer';

const columnHelper = createColumnHelper<Customer>();

const columns = [
  columnHelper.accessor('number', {
    header: 'Kundennummer',
    cell: (info) => info.getValue(),
  }),
  columnHelper.accessor('name', {
    header: 'Name',
    cell: (info) => info.getValue(),
  }),
];

export default function CustomerList() {
  const navigate = useNavigate();
  const { data: customers, isLoading, error } = useCustomers();
  const [sorting, setSorting] = useState<SortingState>([]);

  const table = useReactTable({
    data: customers || [],
    columns,
    state: {
      sorting,
    },
    onSortingChange: setSorting,
    getCoreRowModel: getCoreRowModel(),
    getSortedRowModel: getSortedRowModel(),
  });

  if (isLoading) {
    return (
      <div className="loading">
        <p>Lade Kunden...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error">
        <p>Fehler beim Laden der Kunden: {error.message}</p>
      </div>
    );
  }

  return (
    <div className="customer-list">
      <div className="customer-list-header">
        <h2>Kundenliste</h2>
        <button className="button button-primary" onClick={() => navigate('/customers/new')}>
          Neuer Kunde
        </button>
      </div>

      <table className="table">
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th key={header.id}>
                  {header.isPlaceholder ? null : (
                    <div
                      className={header.column.getCanSort() ? 'cursor-pointer select-none' : ''}
                      onClick={header.column.getToggleSortingHandler()}
                    >
                      {flexRender(header.column.columnDef.header, header.getContext())}
                      {{
                        asc: ' 🔼',
                        desc: ' 🔽',
                      }[header.column.getIsSorted() as string] ?? null}
                    </div>
                  )}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row) => (
            <tr
              key={row.id}
              className="table-row-clickable"
              onClick={() => navigate(`/customers/${row.original.number}`)}
            >
              {row.getVisibleCells().map((cell) => (
                <td key={cell.id}>{flexRender(cell.column.columnDef.cell, cell.getContext())}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>

      {(!customers || customers.length === 0) && (
        <p className="empty-state">Keine Kunden vorhanden</p>
      )}
    </div>
  );
}
